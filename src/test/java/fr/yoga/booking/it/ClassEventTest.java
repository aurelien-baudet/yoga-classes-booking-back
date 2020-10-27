package fr.yoga.booking.it;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.icegreen.greenmail.junit5.GreenMailExtension;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Credentials;
import fr.yoga.booking.domain.account.Preferences;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonDifficulty;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.SubscriptionRepository;
import fr.yoga.booking.repository.TeacherRepository;
import fr.yoga.booking.service.business.BookingService;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.ReminderProperties;
import fr.yoga.booking.service.business.ReminderService;
import fr.yoga.booking.service.business.SubscriptionService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.technical.event.ClassStarted;
import fr.yoga.booking.service.technical.scheduling.SchedulingHelper;
import fr.yoga.booking.service.technical.scheduling.Trigger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(properties = {
	"security.enabled=false",
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}"
})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
class ClassEventTest {
	@Autowired @RegisterExtension GreenMailExtension greenMail;
	@Autowired @RegisterExtension JsmppServerExtension smppServer;

	@Autowired UserService userService;
	@Autowired StudentRepository studentRepository;
	@Autowired TeacherRepository teacherRepository;
	@Autowired SubscriptionRepository subscriptionsRepository;
	@Autowired ClassService classService;
	@Autowired ScheduledClassRepository classRepository;
	@SpyBean SubscriptionService subscriptionService;
	@Autowired BookingService bookingService;
	@Autowired ReminderService reminderService;
	@Autowired ReminderProperties reminderProps;
	@SpyBean SchedulingHelper helper;
	
//	@MockBean SchedulingProperties schedulingProperties;
	
	Teacher cyril;
	Student odile;
	Student simon;
	List<ScheduledClass> classes;
	
	
	@BeforeEach
	void setup() throws AccountException, ScheduledClassException {
		classes = new ArrayList<>();
		cyril = userService.registerTeacher("Cyril", new Credentials("cyril", "foo"), null);
		odile = userService.registerStudent("Odile Deray", new Credentials("odile.deray", "foo"), new ContactInfo("odile.deray@yopmail.com", "+262601020304"), new Preferences());
		simon = userService.registerStudent("Simon Jeremy", new Credentials("simon.jeremy", "foo"), new ContactInfo("simon.jeremy@yopmail.com", "+262600000000"), new Preferences());
	}
	
	@AfterEach
	void cleanup() {
		teacherRepository.deleteAll();
		studentRepository.deleteAll();
		subscriptionsRepository.deleteAll();
		classRepository.deleteAll();
		helper.clean();
		
	}
	
	@Test
	void triggerStartEventToUpdateSubscriptions() throws ScheduledClassException {
		ScheduledClass class1 = scheduleClass("1", now().plus(1, SECONDS));
		ScheduledClass class2 = scheduleClass("2", now().plus(3, SECONDS));
		
		waitForStartEventTriggered(class1);
		waitForStartEventTriggered(class2);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void triggerStartEventToUpdateSubscriptionsEvenIfRestartedButOnlyForFutureClassesOrNeverTriggered() throws ScheduledClassException {
		Instant now = now();
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass neverTriggered = scheduleClass("-4", now.minus(4, SECONDS));
		doCallRealMethod().when(helper).schedule(any(Trigger.class));
		ScheduledClass pastClass3 = scheduleClass("-3", now.minus(3, SECONDS));
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass pastClass2 = scheduleClass("-2", now.minus(2, SECONDS));
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass pastClass1 = scheduleClass("-1", now.minus(1, SECONDS));
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass class1 = scheduleClass("1", now.plus(1, SECONDS));
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass class2 = scheduleClass("2", now.plus(2, SECONDS));
		doNothing().when(helper).schedule(any(Trigger.class));
		ScheduledClass class3 = scheduleClass("3", now.plus(3, SECONDS));

		assertThatNeverTriggered(neverTriggered);
		waitForStartEventTriggered(pastClass3);

		// simulate restart
		log.info("Restarting");
		helper.clean();
		waitFor(2, SECONDS);
		doCallRealMethod().when(helper).schedule(any(Trigger.class));
		classService.restoreTriggersAfterReboot(null);
		log.info("Restarted");

		assertThatNeverTriggered(neverTriggered);
		assertThatTriggeredOnlyOnce(pastClass3);
		waitForStartEventTriggered(pastClass2);
		waitForStartEventTriggered(pastClass1);
		waitForStartEventTriggered(class1);
		waitForStartEventTriggered(class2);
		waitForStartEventTriggered(class3);
	}

	private void assertThatNeverTriggered(ScheduledClass scheduledClass) {
		verify(subscriptionService, never()).updateSubscriptionsWhenClassIsStarted(eq(new ClassStarted(scheduledClass)));
	}

	private void assertThatTriggeredOnlyOnce(ScheduledClass scheduledClass) {
		verify(subscriptionService).updateSubscriptionsWhenClassIsStarted(eq(new ClassStarted(scheduledClass)));
	}

	private void waitForStartEventTriggered(ScheduledClass scheduledClass) {
		await().atMost(5, TimeUnit.SECONDS).ignoreExceptions().until(() -> {
			verify(subscriptionService).updateSubscriptionsWhenClassIsStarted(eq(new ClassStarted(scheduledClass)));
			return true;
		});
	}
	
	
	private void waitFor(int amount, ChronoUnit unit) {
		Instant start = now();
		await().until(() -> now().isAfter(start.plus(amount, unit)));
	}

	private ScheduledClass scheduleClass(String name, Instant start) throws ScheduledClassException {
		Lesson lesson = classService.register(new LessonInfo(name, "", 5, emptyList(), new LessonDifficulty()), new Place("villa", "foo", new ArrayList<>()), cyril);
		return classService.schedule(lesson, start, start.plus(1, SECONDS));
	}
}
