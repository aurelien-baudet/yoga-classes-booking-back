package fr.yoga.booking.domain.notification;

public interface PushNotification {
	String getTitle();
	String getMessage();
	PushNotificationData getData();
}
