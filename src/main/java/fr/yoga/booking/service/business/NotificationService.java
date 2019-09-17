package fr.yoga.booking.service.business;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.BookedNotificationData;
import fr.yoga.booking.domain.notification.ClassCanceledNotificationData;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotificationData;
import fr.yoga.booking.domain.notification.PlaceChangedNotificationData;
import fr.yoga.booking.domain.notification.PushNotificationData;
import fr.yoga.booking.domain.notification.ReminderNotificationData;
import fr.yoga.booking.domain.notification.UnbookedNotificationData;
import fr.yoga.booking.domain.notification.UserPushToken;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.repository.PushNotificationTokenRepository;
import fr.yoga.booking.service.business.exception.NotificationException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.technical.notification.FcmPushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final PushNotificationTokenRepository pushNotificationTokenRepository;
	private final UserService userService;
	private final FcmPushNotificationService fcmService;
	
	public void registerNotificationTokenForUser(User user, String token) {
		if(!pushNotificationTokenRepository.existsByUserIdAndToken(user.getId(), token)) {
			pushNotificationTokenRepository.save(new UserPushToken(user, token));
		}
	}

	public void unregisterNotificationTokenForUser(User user) {
		pushNotificationTokenRepository.deleteByUserId(user.getId());
	}

	public void classCanceled(ScheduledClass scheduledClass, CancelData additionalInfo) {
		log.info("[{}] class canceled. Message: {}", scheduledClass.getId(), additionalInfo.getMessage());
		for(StudentInfo student : scheduledClass.allStudents()) {
			notify(student, new ClassCanceledNotificationData(scheduledClass, additionalInfo));
		}
	}

	public void placeChanged(ScheduledClass scheduledClass, Place oldPlace, Place newPlace) {
		log.info("[{}] place changed {} -> {}", scheduledClass.getId(), oldPlace.getName(), newPlace.getName());
		for(StudentInfo student : scheduledClass.allStudents()) {
			notify(student, new PlaceChangedNotificationData(scheduledClass, oldPlace, newPlace));
		}
	}

	public void booked(ScheduledClass bookedClass, StudentInfo student, User bookedBy) {
		log.info("[{}] booked for {}", bookedClass.getId(), student.getDisplayName());
		notify(student, new BookedNotificationData(bookedClass, student));
	}

	public void unbooked(ScheduledClass bookedClass, StudentInfo student, User canceledBy) {
		log.info("[{}] unbooked for {}", bookedClass.getId(), student.getDisplayName());
		notify(student, new UnbookedNotificationData(bookedClass, student));
	}

	public void freePlaceBooked(ScheduledClass scheduledClass, Booking firstWaiting) {
		log.info("[{}] place available => automatically booked for {}", scheduledClass.getId(), firstWaiting.getStudent().getDisplayName());
		notify(firstWaiting.getStudent(), new FreePlaceBookedNotificationData(scheduledClass, firstWaiting.getStudent()));
	}

	public void reminder(ScheduledClass nextClass, List<StudentInfo> approvedStudents) {
		log.info("[{}] remind students about next class", nextClass.getId());
		for(StudentInfo student : approvedStudents) {
			notify(student, new ReminderNotificationData(nextClass));
		}
	}

	private void notify(StudentInfo student, PushNotificationData data) {
		try {
			// TODO: handle user preferences
			if(student.isRegistered()) {
				sendPushNotification(student, data);
			} else {
				// TODO: send email or SMS for unregistered user
			}
		} catch(NotificationException | UserException e) {
			log.error("Failed to send push notification to "+student.getDisplayName(), e);
			// TODO: handle correctly errors
		}
	}

	private void sendPushNotification(StudentInfo student, PushNotificationData data) throws NotificationException, UserException {
		UserPushToken mapping = pushNotificationTokenRepository.findFirstByUserIdOrderByRegistrationDateDesc(student.getId());
		if(mapping != null) {
			fcmService.sendPushNotification(userService.getUser(student.getId()), mapping.getToken(), data);
		}
	}
}
