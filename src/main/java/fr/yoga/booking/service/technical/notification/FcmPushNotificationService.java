package fr.yoga.booking.service.technical.notification;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidConfig.Priority;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.PushNotification;
import fr.yoga.booking.service.business.exception.NotificationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FcmPushNotificationService {
	private final FirebaseMessaging fcm;
	
	public void sendPushNotification(User user, String token, PushNotification notification) throws NotificationException {
	    try {
		AndroidConfig androidConfig = AndroidConfig.builder()
//	        .setTtl(Duration.ofMinutes(2).toMillis())
//			.setCollapseKey("personal")
//	        .setPriority(Priority.HIGH)
//	        .setNotification(AndroidNotification.builder().setTag("personal").build())
	        .build();

	    ApnsConfig apnsConfig = ApnsConfig.builder()
	        .setAps(Aps.builder().setCategory("personal").setThreadId("personal").build())
	        .build();

	    Message msg = Message.builder()/*.putAllData(data)*/
	    	.putAllData(new BeanUtilsBean2().describe(notification.getData()))
    		.setToken(token)
	        .setApnsConfig(apnsConfig)
	        .setAndroidConfig(androidConfig)
//	        .setNotification(notification.getTitle() == null ? null : new Notification(notification.getTitle(), notification.getMessage()))
	        .build();

			String response = fcm.sendAsync(msg).get();
			// TODO: retry if failed ?
		} catch (InterruptedException | ExecutionException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new FcmPushNotificationException(user, token, null, e);
		}		
	}


}
