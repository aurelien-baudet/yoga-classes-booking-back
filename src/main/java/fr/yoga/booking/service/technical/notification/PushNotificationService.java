package fr.yoga.booking.service.technical.notification;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.service.business.exception.NotificationException;

public interface PushNotificationService {

	void sendPushNotification(User user, String token, Notification notification) throws NotificationException;

}