package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;

public class TooLateToUnbookException extends BookingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TooLateToUnbookException(ScheduledClass bookedClass, StudentRef student) {
		super(bookedClass, student, "Can't cancel booking for " + student.getDisplayName() + " because it's too late");
	}
}
