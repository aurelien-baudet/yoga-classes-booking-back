package fr.yoga.booking.e2e;

import static java.time.temporal.ChronoUnit.HOURS;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;

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
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Image;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.NotificationException;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.technical.notification.OneSignalPushNotificationService;

@SpringBootTest
@ActiveProfiles("test")
public class OneSignalPushNotificationServiceTest {
	@Mock ScheduledClass bookedClass;
	@Mock User user;
	@Mock StudentRef studentRef;
	@Mock Student student;
	@Mock Lesson lesson;
	@Mock LessonInfo lessonInfo;
	@Mock Teacher teacher;
	@Mock Place place;
	@Mock Place newPlace;
	@Mock Image newPlaceImage;
	@Mock CancelData cancelData;
	@Mock ContactInfo contact;
	@MockBean UserService userService;
	String token = "5bbcaf25-d3cd-4e4d-ac30-dba9fd719888";
	
	@Autowired OneSignalPushNotificationService onesignalService;

	String description = "Ce cours est destiné à ceux qui veulent éliminer les toxines accumulées pendant le weekend, à ceux qui veulent s’assouplir et développer leurs muscles profonds à travers une pratique dynamique.\n" + 
			"Le lundi c'est un cours orienté fluidité.\n" + 
			"\n" + 
			"Dans ce cours, on reprendra les postures de base du yoga, on travaillera sur tout le corps de manière équilibrée dans une séquence adaptée au niveau de chacun.\n" + 
			"\n" + 
			"Des tapis seront mis à disposition. \n" + 
			"Participation libre. \n" + 
			"Place limitées , me contactez en MP.";
	
	@BeforeEach
	public void setup() throws MalformedURLException, PlaceException, UserException {
		when(userService.getRegisteredStudent(anyString())).thenReturn(student);
		when(student.getContact()).thenReturn(contact);
		when(user.getDisplayName()).thenReturn("Aurélien");
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
		when(place.getId()).thenReturn("456789");
		when(place.getName()).thenReturn("Villa");
		when(place.getAddress()).thenReturn("25 rue Saint Expedit, Saint Pierre");
		when(newPlace.getName()).thenReturn("Terre Sainte");
		when(newPlace.getAddress()).thenReturn("128 rue Amiral Lacaze, Saint Pierre");
		when(newPlace.getMaps()).thenReturn(Arrays.asList(newPlaceImage));
		when(newPlaceImage.getSize()).thenReturn("SMALL");
		when(newPlaceImage.getType()).thenReturn("STATIC_MAP");
		when(newPlaceImage.getUrl()).thenReturn(new URL("https://drive.google.com/uc?export=view&id=1Ukd3cxRvnOc6IdHiGJy8BAYdPQpM6mX_"));
		when(cancelData.getMessage()).thenReturn("Cours annulé en raison de la pluie...\nEt j'ai la flemme");
	}


	@Test
	@EnabledIf("#{systemProperties['onesignal.api-key'] != null && systemProperties['onesignal.app-id'] != null}")
	public void placeChanged() throws MessagingException, UnreachableUserException, NotificationException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		onesignalService.sendPushNotification(user, token, new PlaceChangedNotification(bookedClass, place, newPlace));
	}

	@Test
	@EnabledIf("#{systemProperties['onesignal.api-key'] != null && systemProperties['onesignal.app-id'] != null}")
	public void classCanceled() throws MessagingException, UnreachableUserException, NotificationException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		onesignalService.sendPushNotification(user, token, new ClassCanceledNotification(bookedClass, cancelData));
	}

	@Test
	@EnabledIf("#{systemProperties['onesignal.api-key'] != null && systemProperties['onesignal.app-id'] != null}")
	public void freePlaceBooked() throws MessagingException, UnreachableUserException, NotificationException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		onesignalService.sendPushNotification(user, token, new FreePlaceBookedNotification(bookedClass, studentRef));
	}

	@Test
	@EnabledIf("#{systemProperties['onesignal.api-key'] != null && systemProperties['onesignal.app-id'] != null}")
	public void availablePlace() throws MessagingException, UnreachableUserException, NotificationException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		onesignalService.sendPushNotification(user, token, new AvailablePlaceNotification(bookedClass, studentRef));
	}

	@Test
	@EnabledIf("#{systemProperties['onesignal.api-key'] != null && systemProperties['onesignal.app-id'] != null}")
	public void reminder() throws MessagingException, UnreachableUserException, NotificationException {
		when(bookedClass.isApprovedFor(Mockito.any())).thenReturn(true);
		onesignalService.sendPushNotification(user, token, new ReminderNotification(bookedClass, studentRef));
	}
}
