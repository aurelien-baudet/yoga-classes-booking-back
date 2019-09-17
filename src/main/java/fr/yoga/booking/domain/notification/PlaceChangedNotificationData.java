package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceChangedNotificationData implements PushNotificationData {
	private final ScheduledClass scheduledClass;
	private final Place oldPlace;
	private final Place newPlace;

	@Override
	public NotificationType getType() {
		return NotificationType.PLACE_CHANGED;
	}

	public String getClassId() {
		return scheduledClass.getId();
	}
	
	public String getOldPlaceId() {
		return oldPlace.getId();
	}
	
	public String getNewPlaceId() {
		return newPlace.getId();
	}
}
