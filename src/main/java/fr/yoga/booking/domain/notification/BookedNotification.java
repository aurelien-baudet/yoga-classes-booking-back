package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BookedNotification implements Notification {
	private final ScheduledClass bookedClass;
	private final StudentRef bookedFor;

	@Override
	public NotificationType getType() {
		return NotificationType.BOOKED;
	}

	public boolean isApproved() {
		return bookedClass.isApprovedFor(bookedFor);
	}
}
