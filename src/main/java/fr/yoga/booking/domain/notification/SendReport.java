package fr.yoga.booking.domain.notification;

import fr.sii.ogham.core.message.Message;
import fr.sii.ogham.email.message.Email;
import fr.sii.ogham.sms.message.Sms;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Data;

@Data
public class SendReport {
	private final StudentRef student;
	private final Notification notification;
	private boolean success;
	private MessageStatus pushNotification;
	private MessageStatus email;
	private MessageStatus sms;
	
	@Data
	public static class MessageStatus {
		private final boolean sent;
		private final Throwable failure;
		
		public static MessageStatus success() {
			return new MessageStatus(true, null);
		}
		
		public static MessageStatus failed(Throwable failure) {
			return new MessageStatus(false, failure);
		}
	}

	public void markPushNotificationSent() {
		pushNotification = MessageStatus.success();
	}

	public void markPushNotificationFailed(Throwable failure) {
		pushNotification = MessageStatus.failed(failure);
	}

	public void markSent(Message message) {
		if (isEmail(message)) {
			email = MessageStatus.success();
		}
		if (isSms(message)) {
			sms = MessageStatus.success();
		}
	}

	public void markEmailAndSmsFailed(Throwable failure) {
		email = MessageStatus.failed(failure);
		sms = MessageStatus.failed(failure);
	}

	private boolean isEmail(Message message) {
		return message instanceof Email;
	}

	private boolean isSms(Message message) {
		return message instanceof Sms;
	}
}
