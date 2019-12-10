package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClassCanceledNotification implements Notification {
	private final ScheduledClass canceledClass;
	private final CancelData additionalInfo;

	@Override
	public NotificationType getType() {
		return NotificationType.CANCELED;
	}

}
