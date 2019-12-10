package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlaceChangedNotification implements Notification {
	private final ScheduledClass scheduledClass;
	private final Place oldPlace;
	private final Place newPlace;

	@Override
	public NotificationType getType() {
		return NotificationType.PLACE_CHANGED;
	}

}
