package fr.yoga.booking.ft;

import static fr.yoga.booking.util.DateUtil.midday;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
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
import fr.yoga.booking.domain.subscription.UserSubscriptions;
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
import fr.yoga.booking.service.business.exception.reservation.BookingException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.technical.scheduling.SchedulingHelper;

@SpringBootTest(properties = {
	"security.enabled=false",
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}",
	"scheduling.enable-class-events=false"
})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
class SubscriptionReminderTest {
	@Autowired @RegisterExtension GreenMailExtension greenMail;
	@Autowired @RegisterExtension JsmppServerExtension smppServer;

	@Autowired UserService userService;
	@Autowired StudentRepository studentRepository;
	@Autowired TeacherRepository teacherRepository;
	@Autowired SubscriptionRepository subscriptionsRepository;
	@Autowired ClassService classService;
	@Autowired ScheduledClassRepository classRepository;
	@Autowired SubscriptionService subscriptionService;
	@Autowired BookingService bookingService;
	@Autowired ReminderService reminderService;
	@Autowired ReminderProperties reminderProps;
	@Autowired SchedulingHelper helper;
	
	@MockBean TaskScheduler scheduler;
	@Captor ArgumentCaptor<Runnable> runnableCaptor;
	@Captor ArgumentCaptor<Instant> scheduledAtCaptor;
	
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
		for (int i=0 ; i<20 ; i++) {
			Lesson lesson = classService.register(new LessonInfo("lesson "+i, "", 5, emptyList(), new LessonDifficulty()), new Place("villa", "foo", new ArrayList<>()), cyril);
			Instant start = now().plus(i, DAYS).truncatedTo(HOURS);
			classes.add(classService.schedule(lesson, start, start.plus(1, HOURS)));
		}
	}
	
	@AfterEach
	void cleanup() {
		teacherRepository.deleteAll();
		studentRepository.deleteAll();
		subscriptionsRepository.deleteAll();
		classRepository.deleteAll();
		helper.clean();
		
	}
	
	@DisplayName("As student that took part in class without paying and without subscription I am notified before next class to bring money")
	@Test
	void notifyForUnpaidClassesBeforeNextClass() throws BookingException {
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson in future (no notification)
		bookingService.book(classes.get(4), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Notify for unpaid class
		assertRegisteredReminder(odile, triggerBeforeNextClassForLowBalance(classes.get(1)));
	}
	
	@DisplayName("As student having not enough remaining payed lessons I am notified before next class to renew subscription")
	@Test
	void notifyForLowBalance() throws BookingException {
		// Odile bought 3 lessons
		subscriptionService.addPaidClasses(odile, 3);
		// Odile takes part in 2 lessons
		subscriptionService.takePartInClass(odile, classes.get(0));
		subscriptionService.takePartInClass(odile, classes.get(1));
		
		// simulate scheduling (nothing to register)
		reminderService.registerRemindersForSubscriptions();
		
		// Odile books 1 lesson
		bookingService.book(classes.get(2), odile, odile);
		// Odile books 1 lesson in future
		bookingService.book(classes.get(4), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeNextClassForLowBalance(classes.get(2)));
	}

	@DisplayName("As student having month subscription that is about to expire I am notified before next class to renew subscription")
	@Test
	void notifyBeforeExpirationOfMonthCardBeforeNextClass() throws BookingException {
		// Odile bought month card
		subscriptionService.addMonthCard(odile, now().minus(26, DAYS));
		
		// simulate scheduling (nothing to register)
		reminderService.registerRemindersForSubscriptions();
		
		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeNextClassForMonthCardExpiration(classes.get(1)));
	}
	
	@DisplayName("As student having month subscription that is about to expire I am notified before expiration to renew subscription")
	@Test
	void notifyBeforeExpirationOfMonthCard() {
		// Odile bought month card
		subscriptionService.addMonthCard(odile, now().minus(26, DAYS));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeMonthCardExpiration(odile));
	}

	@DisplayName("As student having annual subscription that is about to expire I am notified before next class to renew subscription")
	@Test
	void notifyBeforeExpirationOfAnnualCardBeforeNextClass() throws BookingException {
		// Odile bought annual card
		subscriptionService.addAnnualCard(odile, now().minus(356, DAYS));
		
		// simulate scheduling (nothing to register)
		reminderService.registerRemindersForSubscriptions();
		
		// Odile books 1 lesson
		bookingService.book(classes.get(2), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeNextClassForAnnualCardExpiration(classes.get(2)));
	}
	
	@DisplayName("As student having annual subscription that is about to expire I am notified before expiration to renew subscription")
	@Test
	void notifyBeforeExpirationOfAnnualCard() {
		// Odile bought annual card
		subscriptionService.addAnnualCard(odile, now().minus(356, DAYS));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeAnnualCardExpiration(odile));
	}
	
	@DisplayName("As student having month subscription that is about to expire and having not enough remaining lessons I am notified before next class and before expiration to renew subscription")
	@Test
	void monthPriorityOverRaminingLessons() throws BookingException {
		// Odile bought 3 lessons
		subscriptionService.addPaidClasses(odile, 3);
		// Odile took part in 2 lessons
		subscriptionService.takePartInClass(odile, classes.get(0));
		subscriptionService.takePartInClass(odile, classes.get(1));
		// Odile bought month card
		subscriptionService.addMonthCard(odile, now().minus(26, DAYS));
		
		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeMonthCardExpiration(odile));
		assertRegisteredReminder(odile, triggerBeforeNextClassForMonthCardExpiration(classes.get(1)));
	}
	
	
	@DisplayName("As student that took part in class and paid for them I am not notified to bring money")
	@Test
	void dontNotifyBeforeNextClassIfClassesArePaid() throws BookingException {
		// Odile bought 4 lessons
		subscriptionService.addPaidClasses(odile, 4);
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson in future (no notification)
		bookingService.book(classes.get(4), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Notify for unpaid class
		assertNoRegisteredReminders(odile);
	}
	
	@DisplayName("As student with valid month card I am not notified at all")
	@Test
	void dontNotifyIfValidMonthCard() throws BookingException {
		// Odile bought month card
		subscriptionService.addMonthCard(odile, now().minus(5, DAYS));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();

		// Odile books 1 lesson in future (no notification)
		bookingService.book(classes.get(4), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Notify for unpaid class
		assertNoRegisteredReminders(odile);
	}

	
	@DisplayName("As student having month subscription that is already expired I am notified before next class to renew subscription")
	@Test
	void monthCardAlreadyExpired() throws BookingException {
		// Odile bought month card
		subscriptionService.addMonthCard(odile, now().minus(40, DAYS));
		
		// Odile books 1 lesson
		bookingService.book(classes.get(1), odile, odile);
		
		// simulate scheduling
		reminderService.registerRemindersForSubscriptions();
		
		// Remind to bring money
		assertRegisteredReminder(odile, triggerBeforeNextClassForLowBalance(classes.get(1)));
	}
	
	private Instant triggerBeforeNextClassForLowBalance(ScheduledClass scheduledClass) {
		return scheduledClass.getStart().minus(reminderProps.getSubscription().getRemainingClasses().getTriggerBeforeNextClass().first());
	}

	private Instant triggerBeforeNextClassForMonthCardExpiration(ScheduledClass scheduledClass) {
		return scheduledClass.getStart().minus(reminderProps.getSubscription().getMonthCard().getTriggerBeforeNextClass().first());
	}

	private Instant triggerBeforeNextClassForAnnualCardExpiration(ScheduledClass scheduledClass) {
		return scheduledClass.getStart().minus(reminderProps.getSubscription().getAnnualCard().getTriggerBeforeNextClass().first());
	}
	
	private Instant triggerBeforeMonthCardExpiration(Student student) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		return midday(subscription.getMonthCard().getEnd().minus(reminderProps.getSubscription().getMonthCard().getTriggerBeforeExpiration().first()));
	}
	
	private Instant triggerBeforeAnnualCardExpiration(Student student) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		return midday(subscription.getAnnualCard().getEnd().minus(reminderProps.getSubscription().getAnnualCard().getTriggerBeforeExpiration().first()));
	}

	private void assertRegisteredReminder(Student student, Instant before) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		assertNotNull(subscription, "subscription for "+student.getDisplayName()+" should exist");
		verify(scheduler).schedule(any(), eq(before.truncatedTo(HOURS)));
	}
	
	private void assertNoRegisteredReminders(Student student) {
		verify(scheduler, never()).schedule(any(), any(Instant.class));
	}
}
