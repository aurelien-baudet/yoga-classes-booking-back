package fr.yoga.booking.service.technical.notification;

import com.currencyfair.onesignal.model.notification.NotificationRequest;
import com.currencyfair.onesignal.model.notification.NotificationRequestBuilder;

import fr.yoga.booking.domain.notification.Notification;

public interface OneSignalNotificationConverter {
	NotificationRequest toNotificationRequest(Notification notification, NotificationRequestBuilder builder);
}
