package fr.yoga.booking.domain.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookedNotification implements PushNotification {
	private final ScheduledClass bookedClass;
	private final StudentInfo bookedFor;

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
		return new BookedNotificationData(bookedClass, bookedFor);
	}
	
	@Getter
	@RequiredArgsConstructor
	public static class BookedNotificationData implements PushNotificationData {
		@JsonIgnore
		private final ScheduledClass bookedClass;
		@JsonIgnore
		private final StudentInfo bookedFor;
		
		@Override
		public NotificationType getType() {
			return NotificationType.BOOKED;
		}
		
		public String getBookedClassId() {
			return bookedClass.getId();
		}
		
		public String getStudentId() {
			return bookedFor.getId();
		}
		
		public String getStudentDisplayName() {
			return bookedFor.getDisplayName();
		}
		
		public boolean isApproved() {
			return bookedClass.isApprovedFor(bookedFor);
		}
	}

}
