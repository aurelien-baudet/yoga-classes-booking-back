package fr.yoga.booking.it;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.testing.assertion.OghamAssertions;
import fr.sii.ogham.testing.extension.junit.sms.config.ServerConfig;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.sms.simulator.jsmpp.JSMPPServer;
import fr.sii.ogham.testing.sms.simulator.jsmpp.SubmitSmAdapter;
import fr.yoga.booking.domain.account.ContactInfo;
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
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.ContactService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;

@SpringBootTest(properties = {
	"async.enabled=false",
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=3025",
	"ogham.email.from.default-value=bar@yopmail.com",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=42775",
	"ogham.sms.from.default-value=0700000000"
})
@ActiveProfiles("test")
public class ContactServiceTest {
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
	
	@Autowired ContactService contactService;

	String description = "Ce cours est destiné à ceux qui veulent éliminer les toxines accumulées pendant le weekend, à ceux qui veulent s’assouplir et développer leurs muscles profonds à travers une pratique dynamique.\n" + 
			"Le lundi c'est un cours orienté fluidité.\n" + 
			"\n" + 
			"Dans ce cours, on reprendra les postures de base du yoga, on travaillera sur tout le corps de manière équilibrée dans une séquence adaptée au niveau de chacun.\n" + 
			"\n" + 
			"Des tapis seront mis à disposition. \n" + 
			"Participation libre. \n" + 
			"Place limitées , me contactez en MP.";
	
	GreenMail greenMail;
	JSMPPServer smppServer;
	
	@BeforeEach
	public void setup() throws Exception {
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
		smppServer = new JSMPPServer(new ServerConfig().port(42775).build());
		smppServer.start();
		
		when(student.getDisplayName()).thenReturn("Aurélien");
		when(userService.getContactInfo(any(StudentRef.class))).thenReturn(contact);
//		when(contact.getPhoneNumber()).thenReturn(System.getProperty("sms.to"));
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
	
	@AfterEach
	public void cleanup() {
		try {
			smppServer.stop();
		} catch(Exception e) {
			// ignore
		}
		greenMail.stop();
	}

	@Test
	public void emailForApprovedBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);

		contactService.sendMessage(student, new BookedNotification(bookedClass, student));
		
		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForWaitingBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		
		contactService.sendMessage(student, new BookedNotification(bookedClass, student));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}
	
	@Test
	public void emailForUnbooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");
		
		contactService.sendMessage(student, new UnbookedNotification(bookedClass, student));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForClassCanceled() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new ClassCanceledNotification(bookedClass, cancelData));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForPlaceChanged() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new PlaceChangedNotification(bookedClass, place, newPlace));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForFreePlaceBooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new FreePlaceBookedNotification(bookedClass, student));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}

	@Test
	public void emailForReminder() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getEmail()).thenReturn("foo@yopmail.com");

		contactService.sendMessage(student, new ReminderNotification(bookedClass, student));

		OghamAssertions.assertThat(greenMail.getReceivedMessages()).count(is(1));
	}



	@Test
	public void smsForApprovedBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);

		contactService.sendMessage(student, new BookedNotification(bookedClass, student));
		
		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForWaitingBooking() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(false);
		
		contactService.sendMessage(student, new BookedNotification(bookedClass, student));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}
	
	@Test
	public void smsForUnbooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");
		
		contactService.sendMessage(student, new UnbookedNotification(bookedClass, student));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForClassCanceled() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new ClassCanceledNotification(bookedClass, cancelData));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForPlaceChanged() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new PlaceChangedNotification(bookedClass, place, newPlace));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForFreePlaceBooked() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new FreePlaceBookedNotification(bookedClass, student));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	@Test
	public void smsForReminder() throws MessagingException, UnreachableUserException, UserException {
		when(contact.getPhoneNumber()).thenReturn("0600000000");

		contactService.sendMessage(student, new ReminderNotification(bookedClass, student));

		OghamAssertions.assertThat(getReceivedSms()).count(greaterThanOrEqualTo(1));
	}

	private List<SubmitSm> getReceivedSms() {
		return smppServer.getReceivedMessages().stream()
				.map(SubmitSmAdapter::new)
				.collect(toList());
	}
}
