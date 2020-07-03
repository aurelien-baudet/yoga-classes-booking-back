package fr.yoga.booking.it;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.notification.UnbookedNotification;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.ContactService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;

@SpringBootTest(properties = "async.enabled=false")
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class SmsOrEmailTest {
	@Mock ScheduledClass bookedClass;
	@Mock StudentRef student;
	@Mock Lesson lesson;
	@Mock LessonInfo lessonInfo;
	@Mock Teacher teacher;
	@Mock Place place;
	@Mock Place newPlace;
	@Mock CancelData cancelData;
	@Mock ContactInfo contact;
	@MockBean UserService userService;
	@MockBean MessagingService messagingService;
	
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
		when(student.getDisplayName()).thenReturn("Aurélien");
		when(userService.getContactInfo(any(StudentRef.class))).thenReturn(contact);
		when(student.isRegistered()).thenReturn(false);
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

	// phone number | email			| notif			|| expected
	// _			| _				| booked		|| null
	// _			| _				| cancel		|| null
	// _			| @				| booked		|| email
	// _			| @				| cancel		|| email
	// 06			| _				| booked		|| sms
	// 06			| _				| cancel		|| sms
	// 06			| @				| booked		|| email
	// 06			| @				| cancel		|| sms
	@ParameterizedTest(name = "''{0}'' | ''{1}'' | ''{2}'' || ''{3}''")
	@MethodSource("params")
	public void shouldPreferSmsForImportantMessagesButEmailForNonImportant(String sms, String email, Notification notif, Class<Message> expectedMessageType) throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn(sms);
		when(contact.getEmail()).thenReturn(email);
		
		contactService.sendMessage(student, notif);
		
		verify(messagingService).send(any(expectedMessageType));
	}
	
	Stream<Arguments> params() {
		return Stream.of(
	        arguments(null, "foo@yopmail.com", new BookedNotification(bookedClass, student), Email.class),
	        arguments(null, "foo@yopmail.com", new UnbookedNotification(bookedClass, student), Email.class),
	        arguments(null, "foo@yopmail.com", new FreePlaceBookedNotification(bookedClass, student), Email.class),
	        arguments(null, "foo@yopmail.com", new ReminderNotification(bookedClass, student), Email.class),
	        arguments(null, "foo@yopmail.com", new PlaceChangedNotification(bookedClass, place, newPlace), Email.class),
	        arguments(null, "foo@yopmail.com", new ClassCanceledNotification(bookedClass, cancelData), Email.class),

	        arguments("0600000000", null, new BookedNotification(bookedClass, student), Sms.class),
	        arguments("0600000000", null, new UnbookedNotification(bookedClass, student), Sms.class),
	        arguments("0600000000", null, new FreePlaceBookedNotification(bookedClass, student), Sms.class),
	        arguments("0600000000", null, new ReminderNotification(bookedClass, student), Sms.class),
	        arguments("0600000000", null, new PlaceChangedNotification(bookedClass, place, newPlace), Sms.class),
	        arguments("0600000000", null, new ClassCanceledNotification(bookedClass, cancelData), Sms.class),

	        arguments("0600000000", "foo@yopmail.com", new BookedNotification(bookedClass, student), Email.class),
	        arguments("0600000000", "foo@yopmail.com", new UnbookedNotification(bookedClass, student), Email.class),
	        arguments("0600000000", "foo@yopmail.com", new FreePlaceBookedNotification(bookedClass, student), Email.class),
	        arguments("0600000000", "foo@yopmail.com", new ReminderNotification(bookedClass, student), Email.class),
	        arguments("0600000000", "foo@yopmail.com", new PlaceChangedNotification(bookedClass, place, newPlace), Sms.class),
	        arguments("0600000000", "foo@yopmail.com", new ClassCanceledNotification(bookedClass, cancelData), Sms.class)
		);
	}
}
