package fr.yoga.booking.e2e;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import fr.sii.ogham.core.exception.MessagingException;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.notification.RenewAnnualCardNotification;
import fr.yoga.booking.domain.notification.RenewClassPackageCardNotification;
import fr.yoga.booking.domain.notification.RenewMonthCardNotification;
import fr.yoga.booking.domain.notification.UnbookedNotification;
import fr.yoga.booking.domain.notification.UnpaidClassesNotification;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.subscription.PeriodCard;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.service.business.ContactService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;

@SpringBootTest
@ActiveProfiles("test")
public class ContactServiceEndToEndTest {
	@Mock ScheduledClass bookedClass;
	@Mock StudentRef studentRef;
	@Mock Student student;
	@Mock Lesson lesson;
	@Mock LessonInfo lessonInfo;
	@Mock Teacher teacher;
	@Mock Place place;
	@Mock Place newPlace;
	@Mock CancelData cancelData;
	@Mock ContactInfo contact;
	@Mock UserSubscriptions subscription;
	@Mock PeriodCard card;
	@MockBean UserService userService;
	
	@Autowired ContactService contactService;

	String description = "Ce cours est destiné à ceux qui veulent éliminer les toxines accumulées pendant le weekend, à ceux qui veulent s’assouplir et développer leurs muscles profonds à travers une pratique dynamique.\n" + 
			"Le lundi c'est un cours orienté fluidité.\n" + 
			"\n" + 
			"Dans ce cours, on reprendra les postures de base du yoga, on travaillera sur tout le corps de manière équilibrée dans une séquence adaptée au niveau de chacun.\n" + 
			"\n" + 
			"Des tapis seront mis à disposition. \n" + 
			"Participation libre. \n" + 
			"Place limitées , me contactez en MP.";
	
	@BeforeEach
	public void setup() throws UserException {
		when(userService.getRegisteredStudent(anyString())).thenReturn(student);
		when(student.getContact()).thenReturn(contact);
		when(student.getDisplayName()).thenReturn("Aurélien");
		when(studentRef.getDisplayName()).thenReturn("Aurélien");
		when(contact.getEmail()).thenReturn(System.getProperty("email.to"));
		when(contact.getPhoneNumber()).thenReturn(System.getProperty("sms.to"));
		when(studentRef.isRegistered()).thenReturn(true);
		when(bookedClass.getId()).thenReturn("123456");
		when(bookedClass.getStart()).thenReturn(Instant.now());
		when(bookedClass.getEnd()).thenReturn(Instant.now().plus(1, HOURS));
		when(bookedClass.getLesson()).thenReturn(lesson);
		when(lesson.getInfo()).thenReturn(lessonInfo);
		when(lesson.getPlace()).thenReturn(place);
		when(lesson.getTeacher()).thenReturn(teacher);
		when(lessonInfo.getTitle()).thenReturn("Lundi detox du weekend (cours tous niveaux)");
		when(lessonInfo.getDescription()).thenReturn(description);
		when(teacher.getDisplayName()).thenReturn("Cyril");
		when(place.getName()).thenReturn("Villa");
		when(place.getAddress()).thenReturn("25 rue Saint Expedit, Saint Pierre");
		when(newPlace.getName()).thenReturn("Terre Sainte");
		when(newPlace.getAddress()).thenReturn("128 rue Amiral Lacaze, Saint Pierre");
		when(cancelData.getMessage()).thenReturn("Cours annulé en raison de la pluie...\nEt j'ai la flemme");
		when(subscription.getUnpaidClasses()).thenReturn(2);
		when(subscription.getRemainingClasses()).thenReturn(1);
		when(subscription.getMonthCard()).thenReturn(card);
		when(subscription.getAnnualCard()).thenReturn(card);
		when(subscription.getSubscriber()).thenReturn(studentRef);
		when(card.getEnd()).thenReturn(Instant.now().plus(5, DAYS));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void approvedBooking() throws MessagingException, UnreachableUserException, UserException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void waitingBooking() throws MessagingException, UnreachableUserException, UserException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));
	}
	
	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void unbooked() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new UnbookedNotification(bookedClass, studentRef));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void classCanceled() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new ClassCanceledNotification(bookedClass, cancelData));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void placeChanged() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new PlaceChangedNotification(bookedClass, place, newPlace));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void freePlaceBooked() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new FreePlaceBookedNotification(bookedClass, studentRef));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void availablePlace() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new AvailablePlaceNotification(bookedClass, studentRef));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void reminder() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new ReminderNotification(bookedClass, studentRef));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null}")
	public void resetPasswordByEmail() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendResetPasswordMessage(student, contact.getEmail(), "token-1");
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['ogham.sms.smpp.host'] != null}")
	public void resetPasswordySms() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendResetPasswordMessage(student, contact.getPhoneNumber(), "token-1");
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void unpaidClasses() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new UnpaidClassesNotification(subscription));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void renewClassPackageCard() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new RenewClassPackageCardNotification(subscription));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void renewMonthCard() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new RenewMonthCardNotification(subscription));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void renewAnnualCard() throws MessagingException, UnreachableUserException, UserException {
		contactService.sendMessage(student, new RenewAnnualCardNotification(subscription));
	}
}
