package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FreePlaceBookedNotification implements Notification {
	private final ScheduledClass bookedClass;
	private final StudentInfo student;
	
	@Override
	public NotificationType getType() {
		return NotificationType.FREE_PLACE_AUTOMATICALLY_BOOKED;
	}

}
