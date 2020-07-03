package fr.yoga.booking.service.business;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
	private final UserService userService;
	private final MessagingService messagingService;

	@Async
	public void sendMessage(StudentRef student, Notification notification) throws MessagingException, UnreachableUserException, UserException {
		Message message = prepareMessage(student, notification);
		if(message == null) {
			throw new UnreachableUserException(student, notification);
		}
		messagingService.send(message);
	}

	private Message prepareMessage(StudentRef student, Notification notification) throws UserException {
		if (canReceiveSmsOnly(student) || (canReceiveSms(student) && preferSms(student, notification))) {
			return new Sms()
				.to(getPhoneNumber(student))
				.message().template(toTemplateName(notification), notification);
		}
		if (canReceiveEmail(student)) {
			return new Email()
				.to(getEmail(student))
				.body().template(toTemplateName(notification), notification);
		}
		return null;
	}

	private boolean canReceiveSmsOnly(StudentRef student) throws UserException {
		return canReceiveSms(student) && !canReceiveEmail(student);
	}

	private boolean canReceiveEmail(StudentRef student) throws UserException {
		return getEmail(student) != null && !getEmail(student).isBlank();
	}

	private boolean preferSms(StudentRef student, Notification notification) {
		return notification instanceof PlaceChangedNotification 
				|| notification instanceof ClassCanceledNotification;
	}


	private boolean canReceiveSms(StudentRef student) throws UserException {
		return getPhoneNumber(student) != null && !getPhoneNumber(student).isBlank();
	}

	private String toTemplateName(Notification notification) {
		return notification.getType().name().toLowerCase().replaceAll("_", "-");
	}
	
	private String getEmail(StudentRef student) throws UserException {
		return userService.getContactInfo(student).getEmail();
	}
	
	private String getPhoneNumber(StudentRef student) throws UserException {
		return userService.getContactInfo(student).getPhoneNumber();
	}
}
