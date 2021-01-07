package fr.yoga.booking.service.business;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.AvailablePlaceNotification;
import fr.yoga.booking.domain.notification.BookedNotification;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.FreePlaceBookedNotification;
import fr.yoga.booking.domain.notification.MessageToStudentNotification;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.notification.ReminderNotification;
import fr.yoga.booking.domain.notification.RenewAnnualCardNotification;
import fr.yoga.booking.domain.notification.RenewClassPackageCardNotification;
import fr.yoga.booking.domain.notification.RenewMonthCardNotification;
import fr.yoga.booking.domain.notification.SendReport;
import fr.yoga.booking.domain.notification.UnbookedNotification;
import fr.yoga.booking.domain.notification.UnpaidClassesNotification;
import fr.yoga.booking.domain.notification.UserPushToken;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.repository.PushNotificationTokenRepository;
import fr.yoga.booking.service.business.exception.NotificationException;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.business.security.annotation.CanRegisterNotificationToken;
import fr.yoga.booking.service.business.security.annotation.CanSendMessageToStudents;
import fr.yoga.booking.service.business.security.annotation.CanUnregisterNotificationToken;
import fr.yoga.booking.service.technical.error.UnmanagedError;
import fr.yoga.booking.service.technical.error.UnmanagedErrorRepository;
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
	private final UnmanagedErrorRepository errorRepository;
	
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

	public void unpaidClasses(UserSubscriptions subscription) {
		log.info("[{}] {} has unpaid classes", subscription.getSubscriber().getId(), subscription.getSubscriber().getDisplayName());
		StudentRef student = subscription.getSubscriber();
		notify(student, new UnpaidClassesNotification(subscription));
	}

	public void renewClassPackageCard(UserSubscriptions subscription) {
		log.info("[{}] {} needs to renew class package card", subscription.getSubscriber().getId(), subscription.getSubscriber().getDisplayName());
		StudentRef student = subscription.getSubscriber();
		notify(student, new RenewClassPackageCardNotification(subscription));
	}

	public void renewMonthCard(UserSubscriptions subscription) {
		log.info("[{}] {} needs to renew month card", subscription.getSubscriber().getId(), subscription.getSubscriber().getDisplayName());
		StudentRef student = subscription.getSubscriber();
		notify(student, new RenewMonthCardNotification(subscription));
	}

	public void renewAnnualCard(UserSubscriptions subscription) {
		log.info("[{}] {} needs to renew annual card", subscription.getSubscriber().getId(), subscription.getSubscriber().getDisplayName());
		StudentRef student = subscription.getSubscriber();
		notify(student, new RenewAnnualCardNotification(subscription));
	}
	
	@CanSendMessageToStudents
	public List<SendReport> sendMessageToApprovedStudents(String message, ScheduledClass scheduledClass) {
		Teacher sender = scheduledClass.getLesson().getTeacher();
		List<SendReport> reports = new ArrayList<>();
		for (StudentRef student : scheduledClass.approvedStudents()) {
			reports.add(notify(student, new MessageToStudentNotification(sender, student, message)));
		}
		return reports;
	}

	private SendReport notify(StudentRef student, Notification notification) {
		SendReport report = new SendReport(student, notification);
		if(canReceivePushNotification(student)) {
			tryPushAndFallbackToEmailOrSms(student, notification, report);
		}
		if(!canReceivePushNotification(student) || shouldAlsoReceiveUsingOtherMeansOfCommunication(student, notification)) {
			tryEmailOrSms(student, notification, report);
		}
		return report;
	}

	private void tryPushAndFallbackToEmailOrSms(StudentRef student, Notification notification, SendReport report) {
		try {
			sendPushNotification(student, notification);
			report.markPushNotificationSent();
		} catch(NotificationException | UserException e) {
			log.error("Failed to send push notification to {}", student.getDisplayName(), e);
			report.markPushNotificationFailed(e);
			tryEmailOrSms(student, notification, report);
		}
	}

	private void tryEmailOrSms(StudentRef student, Notification notification, SendReport report) {
		try {
			Message sent = contactService.sendMessage(userService.getRegisteredStudent(student), notification).get();
			report.markSent(sent);
		} catch (MessagingException e) {
			log.error("Failed to send email/sms to {}", student.getDisplayName(), e);
			report.markEmailAndSmsFailed(e);
			errorRepository.save(new UnmanagedError("tryEmailOrSms:sendMessage(student="+student.getId()+", notification="+notification.getType()+")", e));
		} catch (UnreachableUserException e) {
			log.warn("User {} is unreachable (neither phone number nor email provided)", student.getDisplayName());
			log.trace("{}", e.getMessage(), e);
			report.markEmailAndSmsFailed(e);
			errorRepository.save(new UnmanagedError("tryEmailOrSms:sendMessage(student="+student.getId()+", notification="+notification.getType()+")", e));
		} catch (ExecutionException e) {
			log.error("Failed to notify {} due to unexpected error", student.getDisplayName(), e);
			report.markEmailAndSmsFailed(e.getCause());
			errorRepository.save(new UnmanagedError("tryEmailOrSms:sendMessage(student="+student.getId()+", notification="+notification.getType()+")", e));
		} catch (Exception e) {
			log.error("Failed to notify {} due to unexpected error", student.getDisplayName(), e);
			report.markEmailAndSmsFailed(e);
			errorRepository.save(new UnmanagedError("tryEmailOrSms:sendMessage(student="+student.getId()+", notification="+notification.getType()+")", e));
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
