package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReminderNotificationData implements PushNotificationData {
	private final ScheduledClass nextClass;

	@Override
	public NotificationType getType() {
		return NotificationType.REMINDER;
	}
	
	public String getClassId() {
		return nextClass.getId();
	}
}
