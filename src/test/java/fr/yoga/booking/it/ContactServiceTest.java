package fr.yoga.booking.it;

import static java.time.temporal.ChronoUnit.HOURS;
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

import com.google.firebase.messaging.FirebaseMessaging;

import fr.sii.ogham.core.exception.MessagingException;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.notification.UnbookedNotification;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.service.business.ContactService;
import fr.yoga.booking.service.business.exception.UnreachableUserException;

@SpringBootTest
@ActiveProfiles("test")
public class ContactServiceTest {
	@MockBean FirebaseMessaging fcm;
	@Mock ScheduledClass bookedClass;
	@Mock StudentInfo unregisteredStudent;
	@Mock Lesson lesson;
	@Mock LessonInfo lessonInfo;
	@Mock Teacher teacher;
	@Mock Place place;
	@Mock Place newPlace;
	@Mock CancelData cancelData;
	
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
	public void setup() {
		when(unregisteredStudent.getDisplayName()).thenReturn("Aurélien");
		when(unregisteredStudent.getEmail()).thenReturn(System.getProperty("email.to"));
		when(unregisteredStudent.getPhoneNumber()).thenReturn(System.getProperty("sms.to"));
		when(unregisteredStudent.isRegistered()).thenReturn(false);
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
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void approvedBooking() throws MessagingException, UnreachableUserException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		contactService.sendMessage(unregisteredStudent, new BookedNotification(bookedClass, unregisteredStudent));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void waitingBooking() throws MessagingException, UnreachableUserException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		contactService.sendMessage(unregisteredStudent, new BookedNotification(bookedClass, unregisteredStudent));
	}
	
	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void unbooked() throws MessagingException, UnreachableUserException {
		contactService.sendMessage(unregisteredStudent, new UnbookedNotification(bookedClass, unregisteredStudent));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void classCanceled() throws MessagingException, UnreachableUserException {
		contactService.sendMessage(unregisteredStudent, new ClassCanceledNotification(bookedClass, cancelData));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void placeChanged() throws MessagingException, UnreachableUserException {
		contactService.sendMessage(unregisteredStudent, new PlaceChangedNotification(bookedClass, place, newPlace));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void freePlaceBooked() throws MessagingException, UnreachableUserException {
		contactService.sendMessage(unregisteredStudent, new FreePlaceBookedNotification(bookedClass, unregisteredStudent));
	}

	/**
	 * Not a real test. Just use it to send an email or SMS
	 */
	@Test
	@EnabledIf("#{systemProperties['mail.smtp.host'] != null || systemProperties['ogham.sms.smpp.host'] != null}")
	public void reminder() throws MessagingException, UnreachableUserException {
		contactService.sendMessage(unregisteredStudent, new ReminderNotification(bookedClass, unregisteredStudent));
	}
}
