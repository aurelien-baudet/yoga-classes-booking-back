package fr.yoga.booking.ft;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import fr.yoga.booking.domain.subscription.PeriodCard;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.SubscriptionRepository;
import fr.yoga.booking.repository.TeacherRepository;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.SubscriptionService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.user.AccountException;

@SpringBootTest(properties = {
	"security.enabled=false",
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}",
})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
class SubscriptionTest {
	@Autowired @RegisterExtension GreenMailExtension greenMail;
	@Autowired @RegisterExtension JsmppServerExtension smppServer;

	@Autowired UserService userService;
	@Autowired StudentRepository studentRepository;
	@Autowired TeacherRepository teacherRepository;
	@Autowired SubscriptionRepository subscriptionsRepository;
	@Autowired ClassService classService;
	@Autowired ScheduledClassRepository classRepository;
	@Autowired SubscriptionService subscriptionService;
	
	
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
			Lesson lesson = classService.register(new LessonInfo("lesson "+i, "", 5, emptyList(), new LessonDifficulty()), new Place(), cyril);
			classes.add(classService.schedule(lesson, Instant.now(), Instant.now().plus(1, HOURS)));
		}
	}
	
	@AfterEach
	void cleanup() {
		teacherRepository.deleteAll();
		studentRepository.deleteAll();
		subscriptionsRepository.deleteAll();
		classRepository.deleteAll();
	}
	
	@Test
	void asStudentITakePartInSeveralClassesWithoutPaying() {
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertPaidClasses(odile, -1);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertPaidClasses(odile, -2);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertPaidClasses(odile, -3);
	}
	
	@Test
	void asStudentITakePartInSeveralClassesButOnlySomeArePaid() {
		// Odile pays Cyril for a card of 3 classes
		// Cyril marks that Odile has payed the card
		subscriptionService.addPaidClasses(odile, 2);
		assertPaidClasses(odile, 2);

		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertPaidClasses(odile, 1);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertPaidClasses(odile, 0);
		
		// Odile takes part in another lesson (unpaid)
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertPaidClasses(odile, -1);
		
		// Odile takes part in another lesson (unpaid)
		subscriptionService.takePartInClass(odile, classes.get(3));
		assertPaidClasses(odile, -2);
	}

	@Test
	void asStudentIPayForAClassPackageCardAndConsumeAll() {
		// Odile pays Cyril for a card of 3 classes
		// Cyril marks that Odile has payed the card
		subscriptionService.addPaidClasses(odile, 3);
		subscriptionService.addPaidClasses(simon, 3);
		assertPaidClasses(odile, 3);
		assertPaidClasses(simon, 3);
		
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertPaidClasses(odile, 2);
		assertPaidClasses(simon, 3);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertPaidClasses(odile, 1);
		assertPaidClasses(simon, 3);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertPaidClasses(odile, 0);
		assertPaidClasses(simon, 3);
	}
	
	@Test
	void asStudentIPayForAClassPackageCardAndConsumeAllAndFollowALessonEvenIfItHasNotAlreadyBeenPaid() {
		// Odile pays Cyril for a card of 3 classes
		// Cyril marks that Odile has payed the card
		subscriptionService.addPaidClasses(odile, 3);
		assertPaidClasses(odile, 3);
		
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertPaidClasses(odile, 2);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertPaidClasses(odile, 1);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertPaidClasses(odile, 0);
		
		// Odile takes part in a lesson even if she didn't pay for it
		subscriptionService.takePartInClass(odile, classes.get(3));
		assertPaidClasses(odile, -1);
		
		// Odile takes part in a lesson even if she didn't pay for it
		subscriptionService.takePartInClass(odile, classes.get(4));
		assertPaidClasses(odile, -2);
	}

	
	@Test
	void asStudentIPayForAMonthCardAndTakePartInClasses() {
		// Odile pays Cyril for a month card
		// Cyril marks that Odile has payed the card
		subscriptionService.addMonthCard(odile, now());
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 0);
		
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 0);

		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 0);

		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 0);
	}
	

	@Test
	void asStudentIPayedForAMonthCardThatIsNowExpiredAndTakePartInClasses() {
		// Odile pays Cyril for a month card
		// Cyril marks that Odile has payed the card
		// time has passed
		subscriptionService.addMonthCard(odile, now().minus(31, DAYS));
		assertMonthCard(odile, true);
		assertPaidClasses(odile, 0);
		
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertMonthCard(odile, true);
		assertPaidClasses(odile, -1);

		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertMonthCard(odile, true);
		assertPaidClasses(odile, -2);
	}

	@Test
	void asStudentIPayedForAMonthCardThatIsAboutToExpireAndIStillHaveAClassPackageCardAndTakePartInClasses() {
		// Odile pays Cyril for a several classes card
		// Odile pays Cyril for a month card
		// Cyril marks that Odile has payed the cards
		subscriptionService.addPaidClasses(odile, 3);
		subscriptionService.addMonthCard(odile, now().minus(30, DAYS));
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 3);
		
		// Odile takes part in 1 lesson (taken on month card)
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 3);
		
		simulateTimePassed(2, DAYS);

		// Odile takes part in another lesson (taken on several classes card)
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertMonthCard(odile, true);
		assertPaidClasses(odile, 2);
	}



	@Test
	void asStudentIPayedForAAnnualCardThatIsAboutToExpireAndForAMonthCardThatIsAboutToExpireAndIStillHaveAClassPackageCardAndTakePartInClasses() {
		// Odile pays Cyril for a several classes card
		// Odile pays Cyril for a month card
		// Cyril marks that Odile has payed the cards
		subscriptionService.addPaidClasses(odile, 3);
		subscriptionService.addMonthCard(odile, now().minus(28, DAYS));
		subscriptionService.addAnnualCard(odile, now().minus(365, DAYS));
		assertAnnualCard(odile, false);
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 3);
		
		// Odile takes part in 1 lesson (taken on annual card)
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertAnnualCard(odile, false);
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 3);
		
		simulateTimePassed(2, DAYS);

		// Odile takes part in another lesson (taken on month card)
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertAnnualCard(odile, true);
		assertMonthCard(odile, false);
		assertPaidClasses(odile, 3);
		
		simulateTimePassed(2, DAYS);

		// Odile takes part in another lesson (taken on several classes card)
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertAnnualCard(odile, true);
		assertMonthCard(odile, true);
		assertPaidClasses(odile, 2);
	}
	
	@Test
	void asStudentIPayForAClassPackageCardAndConsumeAllAndFollowALessonEvenIfItHasNotAlreadyBeenPaidAndThenIPay() {
		// Odile pays Cyril for a card of 3 classes
		// Cyril marks that Odile has payed the card
		subscriptionService.addPaidClasses(odile, 3);
		assertPaidClasses(odile, 3);
		
		// Odile takes part in 1 lesson
		subscriptionService.takePartInClass(odile, classes.get(0));
		assertPaidClasses(odile, 2);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(1));
		assertPaidClasses(odile, 1);
		
		// Odile takes part in another lesson
		subscriptionService.takePartInClass(odile, classes.get(2));
		assertPaidClasses(odile, 0);
		
		// Odile takes part in a lesson even if she didn't pay for it
		subscriptionService.takePartInClass(odile, classes.get(3));
		assertPaidClasses(odile, -1);
		
		// Odile takes part in a lesson even if she didn't pay for it
		subscriptionService.takePartInClass(odile, classes.get(4));
		assertPaidClasses(odile, -2);
		
		// Odile pays Cyril for a card of 4 classes
		// Cyril marks that Odile has payed the 2 previously unpaid classes
		// Cyril marks that Odile has payed the card (but with 2 fewer classes)
		subscriptionService.addPaidClasses(odile, 4);
		assertPaidClasses(odile, 2);
	}
	
	private void assertPaidClasses(Student student, int numPaid) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		assertNotNull(subscription, "subscription for "+student.getDisplayName()+" should exist");
		assertEquals(numPaid, subscription.getRemainingClasses(), "number of ramining classes should be "+numPaid);
	}

	private void assertAnnualCard(Student student, boolean expired) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		assertNotNull(subscription, "subscription for "+student.getDisplayName()+" should exist");
		assertEquals(expired, subscription.getAnnualCard().expired(), "annual card should be "+(expired ? "expired" : "not expired"));
	}

	private void assertMonthCard(Student student, boolean expired) {
		UserSubscriptions subscription = subscriptionService.getSubscriptionsFor(student);
		assertNotNull(subscription, "subscription for "+student.getDisplayName()+" should exist");
		assertEquals(expired, subscription.getMonthCard().expired(), "month card should be "+(expired ? "expired" : "not expired"));
	}
	
	private void simulateTimePassed(int amount, TemporalUnit unit) {
		for (UserSubscriptions subscription : subscriptionService.getCurrentSubscriptions()) {
			PeriodCard monthCard = subscription.getMonthCard();
			if (monthCard != null) {
				monthCard.setEnd(monthCard.getEnd().minus(amount, unit));
				subscriptionsRepository.save(subscription);
			}
			PeriodCard annualCard = subscription.getAnnualCard();
			if (annualCard != null) {
				annualCard.setEnd(annualCard.getEnd().minus(amount, unit));
				subscriptionsRepository.save(subscription);
			}
		}
	}
}
