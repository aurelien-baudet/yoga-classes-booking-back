package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.util.DateRangeUtil;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlaceChangedNotification implements PushNotification {
	private final ScheduledClass scheduledClass;
	private final Place oldPlace;
	private final Place newPlace;

	@Override
	public String getTitle() {
		return "Changement de lieu";
	}

	@Override
	public String getMessage() {
		return "Le cours du " + DateRangeUtil.format(scheduledClass.getStart(), scheduledClass.getEnd()) + " aura lieu à " + newPlace.getName() + " (" + newPlace.getAddress() + ")";
	}

	@Override
	public PushNotificationData getData() {
		return new PlaceChangedNotificationData(scheduledClass, oldPlace, newPlace);
	}

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
}
