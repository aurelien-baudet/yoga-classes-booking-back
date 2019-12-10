package fr.yoga.booking.service.technical.notification;

import static com.currencyfair.onesignal.model.notification.NotificationRequestBuilder.aNotificationRequest;
import static fr.yoga.booking.domain.notification.NotificationType.CANCELED;
import static fr.yoga.booking.domain.notification.NotificationType.FREE_PLACE_AUTOMATICALLY_BOOKED;
import static fr.yoga.booking.domain.notification.NotificationType.PLACE_CHANGED;
import static fr.yoga.booking.domain.notification.NotificationType.REMINDER;
import static fr.yoga.booking.service.technical.notification.OneSignalNotificationConverters.classCanceledConverter;
import static fr.yoga.booking.service.technical.notification.OneSignalNotificationConverters.freePlaceAutomaticallyBookedConverter;
import static fr.yoga.booking.service.technical.notification.OneSignalNotificationConverters.placeChangedConverter;
import static fr.yoga.booking.service.technical.notification.OneSignalNotificationConverters.reminderConverter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.currencyfair.onesignal.OneSignal;
import com.currencyfair.onesignal.model.notification.NotificationRequestBuilder;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.notification.NotificationType;
import fr.yoga.booking.service.business.exception.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OneSignalPushNotificationService implements PushNotificationService {
	private final OneSignalProperties oneSignalConfig;
	private final Map<NotificationType, OneSignalNotificationConverter> onesignalConverters;

	@Autowired
	public OneSignalPushNotificationService(OneSignalProperties oneSignalConfig) {
		this(oneSignalConfig, new HashMap<>());
//		oneSignalTags.put(BOOKED, placeChangedConverter());
//		oneSignalTags.put(UNBOOKED, placeChangedConverter());
		onesignalConverters.put(FREE_PLACE_AUTOMATICALLY_BOOKED, freePlaceAutomaticallyBookedConverter());
		onesignalConverters.put(PLACE_CHANGED, placeChangedConverter());
		onesignalConverters.put(REMINDER, reminderConverter());
		onesignalConverters.put(CANCELED, classCanceledConverter());
	}

	
	@Override
	public void sendPushNotification(User user, String token, Notification notification) throws NotificationException {
		NotificationRequestBuilder builder = aNotificationRequest()
				.withAppId(oneSignalConfig.getAppId())
				.withIncludePlayerId(token);
		OneSignalNotificationConverter converter = onesignalConverters.get(notification.getType());
		if (converter == null) {
			log.info("No converter for push notification {}", notification.getType());
			return;
		}
		OneSignal.createNotification(oneSignalConfig.getApiKey(), converter.toNotificationRequest(notification, builder));
	}
}
