package fr.yoga.booking.service.technical.notification;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.service.business.exception.NotificationException;
import lombok.Getter;

@Getter
public class FcmPushNotificationException extends NotificationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final User user;
	private final String token;
	private final String message;

	public FcmPushNotificationException(User user, String token, String message, Throwable cause) {
		super("Failed to send push notification to "+user.getDisplayName(), cause);
		this.user = user;
		this.token = token;
		this.message = message;
	}

}
