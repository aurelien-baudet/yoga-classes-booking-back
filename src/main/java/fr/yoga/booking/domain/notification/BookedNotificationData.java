package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookedNotificationData implements PushNotificationData {
	private final ScheduledClass bookedClass;
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
		return bookedClass.getBookings()
				.stream()
				.filter(b -> b.isForStudent(bookedFor))
				.anyMatch(b -> b.isApproved());
	}
}
