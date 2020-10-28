package fr.yoga.booking.it;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.icegreen.greenmail.junit5.GreenMailExtension;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.MessageToStudentNotification;
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

@SpringBootTest(properties = {
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}",
})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
public class ContactServiceTest {
	@Autowired @RegisterExtension GreenMailExtension greenMail;
	@Autowired @RegisterExtension JsmppServerExtension smppServer;

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
	public void setup() throws Exception {
		when(userService.getRegisteredStudent(anyString())).thenReturn(student);
		when(student.getContact()).thenReturn(contact);
		when(studentRef.getDisplayName()).thenReturn("Aurélien");
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

	@Test
	public void emailForApprovedBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);

		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));
		
		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForWaitingBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		
		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}
	
	@Test
	public void emailForUnbooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		
		contactService.sendMessage(student, new UnbookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForClassCanceled() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new ClassCanceledNotification(bookedClass, cancelData));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForPlaceChanged() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new PlaceChangedNotification(bookedClass, place, newPlace));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForFreePlaceBooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new FreePlaceBookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForAvailablePlace() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new AvailablePlaceNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForReminder() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new ReminderNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForUnpaidClasses() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new UnpaidClassesNotification(subscription));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForRenewMonthCard() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new RenewMonthCardNotification(subscription));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForRenewAnnualCard() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new RenewAnnualCardNotification(subscription));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}

	@Test
	public void emailForMessageToStudent() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new MessageToStudentNotification(teacher, studentRef, "contenu du message"));

		OghamAssertions.assertThat(greenMail).receivedMessages().count(is(1));
	}




	@Test
	public void smsForApprovedBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);

		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));
		
		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForWaitingBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		
		contactService.sendMessage(student, new BookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}
	
	@Test
	public void smsForUnbooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		
		contactService.sendMessage(student, new UnbookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForClassCanceled() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new ClassCanceledNotification(bookedClass, cancelData));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForPlaceChanged() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new PlaceChangedNotification(bookedClass, place, newPlace));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForFreePlaceBooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new FreePlaceBookedNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForAvailablePlace() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new AvailablePlaceNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForReminder() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new ReminderNotification(bookedClass, studentRef));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForUnpaidClasses() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new UnpaidClassesNotification(subscription));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForRenewClassPackageCard() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new RenewClassPackageCardNotification(subscription));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForRenewMonthCard() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new RenewMonthCardNotification(subscription));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForRenewAnnualCard() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new RenewAnnualCardNotification(subscription));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForMessageToStudent() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new MessageToStudentNotification(teacher, studentRef, "contenu du message"));

		OghamAssertions.assertThat(smppServer).receivedMessages().count(greaterThanOrEqualTo(1));
	}

}
