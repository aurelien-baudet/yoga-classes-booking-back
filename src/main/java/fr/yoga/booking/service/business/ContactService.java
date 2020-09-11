package fr.yoga.booking.service.business;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.ClassCanceledNotification;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.PlaceChangedNotification;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import fr.yoga.booking.service.business.exception.user.UserException;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
	private final MessagingService messagingService;

	@Async
	public void sendMessage(Student student, Notification notification) throws MessagingException, UnreachableUserException, UserException {
		Message message = prepareMessage(student, notification);
		if(message == null) {
			throw new UnreachableUserException(student, notification);
		}
		messagingService.send(message);
	}

	@Async
	public void sendResetPasswordMessage(Student student, String emailOrPhoneNumber, String token) throws MessagingException {
		Message message = prepareResetPasswordMessage(student, emailOrPhoneNumber, token);
		messagingService.send(message);		
	}
	
	private Message prepareResetPasswordMessage(Student student, String emailOrPhoneNumber, String token) {
		if (isEmail(emailOrPhoneNumber)) {
			return new Email()
					.to(student.getContact().getEmail())
					.body().template("reset-password", new PasswordReset(student, token));
		}
		return new Sms()
				.to(student.getContact().getPhoneNumber())
				.message().template("reset-password", new PasswordReset(student, token));
	}

	private Message prepareMessage(Student student, Notification notification) throws UserException {
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

	private boolean canReceiveSmsOnly(Student student) throws UserException {
		return canReceiveSms(student) && !canReceiveEmail(student);
	}

	private boolean canReceiveEmail(Student student) throws UserException {
		return getEmail(student) != null && !getEmail(student).isBlank();
	}

	private boolean preferSms(Student student, Notification notification) {
		return notification instanceof PlaceChangedNotification 
				|| notification instanceof ClassCanceledNotification;
	}


	private boolean canReceiveSms(Student student) throws UserException {
		return getPhoneNumber(student) != null && !getPhoneNumber(student).isBlank();
	}

	private String toTemplateName(Notification notification) {
		return notification.getType().name().toLowerCase().replaceAll("_", "-");
	}
	
	private String getEmail(Student student) throws UserException {
		return student.getContact().getEmail();
	}
	
	private String getPhoneNumber(Student student) throws UserException {
		return student.getContact().getPhoneNumber();
	}
	
	private boolean isEmail(String str) {
		return str.contains("@");
	}

	@Data
	public static class PasswordReset {
		private final User user;
		private final String token;
	}
}
