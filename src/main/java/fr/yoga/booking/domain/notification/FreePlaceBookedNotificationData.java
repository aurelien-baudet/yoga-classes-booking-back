package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FreePlaceBookedNotificationData implements PushNotificationData {
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
