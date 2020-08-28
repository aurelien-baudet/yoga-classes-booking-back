package fr.yoga.booking.service.business.exception.reservation;

import static fr.yoga.booking.util.DateRangeUtil.format;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public class PlaceAlreadyTakenException extends BookingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlaceAlreadyTakenException(ScheduledClass bookedClass, StudentRef student) {
		super(bookedClass, student, student.getDisplayName() + " has already registered confirmed its presence to class " + format(bookedClass.getStart(), bookedClass.getEnd()));
	}

	public PlaceAlreadyTakenException(ScheduledClass bookedClass, Student student) {
		this(bookedClass, new StudentRef(student));
	}

	public PlaceAlreadyTakenException(ScheduledClass bookedClass, UnregisteredUser student) {
		this(bookedClass, new StudentRef(student));
	}
}
