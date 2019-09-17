package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClassCanceledNotificationData implements PushNotificationData {
	private final ScheduledClass canceledClass;
	private final CancelData additionalInfo;
	
	@Override
	public NotificationType getType() {
		return NotificationType.CANCELED;
	}

	public String getCanceledClassId() {
		return canceledClass.getId();
	}
	
	public String getCancelMessage() {
		return additionalInfo.getMessage();
	}
}
