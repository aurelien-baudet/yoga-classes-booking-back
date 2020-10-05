package fr.yoga.booking.service.business;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.notification.UnbookedNotification;
import fr.yoga.booking.domain.notification.UserPushToken;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.repository.PushNotificationTokenRepository;
import fr.yoga.booking.service.business.exception.NotificationException;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.business.security.annotation.CanRegisterNotificationToken;
import fr.yoga.booking.service.business.security.annotation.CanUnregisterNotificationToken;
import fr.yoga.booking.service.technical.notification.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final PushNotificationTokenRepository pushNotificationTokenRepository;
	private final UserService userService;
	private final PushNotificationService pushService;
	private final ContactService contactService;
	
	@CanRegisterNotificationToken
	public void registerNotificationTokenForUser(User user, String token) {
		if(!pushNotificationTokenRepository.existsByUserIdAndToken(user.getId(), token)) {
			pushNotificationTokenRepository.save(new UserPushToken(user, token));
		}
	}

	@CanUnregisterNotificationToken
	public void unregisterNotificationTokenForUser(User user) {
		pushNotificationTokenRepository.deleteByUserId(user.getId());
	}

	public void classCanceled(ScheduledClass scheduledClass, CancelData additionalInfo) {
		log.info("[{}] class canceled. Message: {}", scheduledClass.getId(), additionalInfo.getMessage());
		for(StudentRef student : scheduledClass.allStudents()) {
			notify(student, new ClassCanceledNotification(scheduledClass, additionalInfo));
		}
	}

	public void placeChanged(ScheduledClass scheduledClass, Place oldPlace, Place newPlace) {
		log.info("[{}] place changed {} -> {}", scheduledClass.getId(), oldPlace.getName(), newPlace.getName());
		for(StudentRef student : scheduledClass.allStudents()) {
			notify(student, new PlaceChangedNotification(scheduledClass, oldPlace, newPlace));
		}
	}

	public void booked(ScheduledClass bookedClass, StudentRef student, User bookedBy) {
		log.info("[{}] booked for {}", bookedClass.getId(), student.getDisplayName());
		notify(student, new BookedNotification(bookedClass, student));
	}

	public void unbooked(ScheduledClass bookedClass, StudentRef student, User canceledBy) {
		log.info("[{}] unbooked for {}", bookedClass.getId(), student.getDisplayName());
		notify(student, new UnbookedNotification(bookedClass, student));
	}

	public void freePlaceBooked(ScheduledClass scheduledClass, Booking firstWaiting) {
		log.info("[{}] place available => automatically booked for {}", scheduledClass.getId(), firstWaiting.getStudent().getDisplayName());
		notify(firstWaiting.getStudent(), new FreePlaceBookedNotification(scheduledClass, firstWaiting.getStudent()));
	}

	public void reminder(ScheduledClass nextClass, List<StudentRef> approvedStudents) {
		log.info("[{}] remind students about next class", nextClass.getId());
		for(StudentRef student : approvedStudents) {
			notify(student, new ReminderNotification(nextClass, student));
		}
	}

	public void availablePlace(ScheduledClass scheduledClass, List<StudentRef> waitingStudents) {
		log.info("[{}] place available => notify students that are waiting", scheduledClass.getId());
		for(StudentRef student : waitingStudents) {
			notify(student, new AvailablePlaceNotification(scheduledClass, student));
		}
	}

	private void notify(StudentRef student, Notification notification) {
		if(canReceivePushNotification(student)) {
			tryPushAndFallbackToEmailOrSms(student, notification);
		}
		if(!canReceivePushNotification(student) || shouldAlsoReceiveUsingOtherMeansOfCommunication(student, notification)) {
			tryEmailOrSms(student, notification);
		}
	}

	private void tryPushAndFallbackToEmailOrSms(StudentRef student, Notification notification) {
		try {
			sendPushNotification(student, notification);
		} catch(NotificationException | UserException e) {
			log.error("Failed to send push notification to {}", student.getDisplayName(), e);
			// TODO: handle correctly errors
			tryEmailOrSms(student, notification);
		}
	}

	private void tryEmailOrSms(StudentRef student, Notification notification) {
		try {
			contactService.sendMessage(userService.getRegisteredStudent(student), notification);
		} catch (MessagingException e) {
			log.error("Failed to send email/sms to {}", student.getDisplayName(), e);
			// TODO: handle correctly errors
		} catch (UnreachableUserException e) {
			log.warn("User {} is unreachable (neither phone number nor email provided)", student.getDisplayName());
			log.trace("{}", e.getMessage(), e);
			// TODO: handle correctly errors
		} catch (Exception e) {
			log.error("Failed to notify {} due to unexpected error", student.getDisplayName(), e);
			// TODO: handle correctly errors
		}
	}

	private boolean canReceivePushNotification(StudentRef student) {
		return student.isRegistered() && pushNotificationTokenRepository.existsByUserId(student.getId());
	}

	private boolean shouldAlsoReceiveUsingOtherMeansOfCommunication(StudentRef student, Notification notification) {
		// TODO: should duplicate information for particular notification (like place change or class canceled for example) ?
		// TODO: unregistered user has option to automatically receive email for booked classes (aim is to receive ics)
		return false;
	}

	private void sendPushNotification(StudentRef student, Notification data) throws NotificationException, UserException {
		UserPushToken mapping = pushNotificationTokenRepository.findFirstByUserIdOrderByRegistrationDateDesc(student.getId());
		if(mapping != null) {
			pushService.sendPushNotification(userService.getUser(student.getId()), mapping.getToken(), data);
		}
	}

}
