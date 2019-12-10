package fr.yoga.booking.service.business;

import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.core.message.content.MultiTemplateContent;
import fr.sii.ogham.core.message.content.TemplateContent;
import fr.sii.ogham.core.service.MessagingService;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.service.business.exception.UnreachableUserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
	private final MessagingService messagingService;

	public void sendMessage(StudentInfo student, Notification notification) throws MessagingException, UnreachableUserException {
		Message message = prepareMessage(student, notification);
		if(message == null) {
			throw new UnreachableUserException(student, notification);
		}
		messagingService.send(message);
	}

	private Message prepareMessage(StudentInfo student, Notification notification) {
		if(canReceiveEmail(student) && preferEmail(student, notification)) {
			return new Email()
					.to(student.getEmail())
					.content(new MultiTemplateContent(toTemplateName(notification), notification));
		}
		if(canReceiveSms(student)) {
			return new Sms()
					.to(student.getPhoneNumber())
					.content(new TemplateContent(toTemplateName(notification), notification));
		}
		return null;
	}

	private boolean canReceiveEmail(StudentInfo student) {
		return student.getEmail() != null;
	}

	private boolean preferEmail(StudentInfo student, Notification notification) {
		// TODO: handle means of communication preferences (prefer email or sms for particular user/notification) ?
		return true;
	}

	private boolean canReceiveSms(StudentInfo student) {
		return student.getPhoneNumber() != null;
	}

	private String toTemplateName(Notification notification) {
		return notification.getType().name().toLowerCase().replaceAll("_", "-");
	}
}
