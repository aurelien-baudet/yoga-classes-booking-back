package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FreePlaceBookedNotification implements PushNotification {
	private final ScheduledClass bookedClass;
	private final StudentInfo student;
	
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
		return new FreePlaceBookedNotificationData(bookedClass, student);
	}

	@Getter
	@RequiredArgsConstructor
	public static class FreePlaceBookedNotificationData implements PushNotificationData {
		private final ScheduledClass bookedClass;
		private final StudentInfo student;
		
		@Override
		public NotificationType getType() {
			return NotificationType.FREE_PLACE_AUTOMATICALLY_BOOKED;
		}
		
		public String getBookedClassId() {
			return bookedClass.getId();
		}
		
		public String getStudentId() {
			return student.getId();
		}
		
		public String getStudentDisplayName() {
			return student.getDisplayName();
		}
	}

}
