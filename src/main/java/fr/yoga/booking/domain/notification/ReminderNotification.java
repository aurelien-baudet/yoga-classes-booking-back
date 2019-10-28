package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReminderNotification implements PushNotification {
	private final ScheduledClass nextClass;

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public PushNotificationData getData() {
		return new ReminderNotificationData(nextClass);
	}
	
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

}
