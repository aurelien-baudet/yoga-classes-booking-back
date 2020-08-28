package fr.yoga.booking.service.business.exception.reservation;

import static fr.yoga.booking.util.DateRangeUtil.format;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public class PlaceAlreadyTakenBySomeoneElseException extends BookingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlaceAlreadyTakenBySomeoneElseException(ScheduledClass bookedClass, StudentRef student) {
		super(bookedClass, student, "Someone else has already confirmed its presence to class " + format(bookedClass.getStart(), bookedClass.getEnd()));
	}

	public PlaceAlreadyTakenBySomeoneElseException(ScheduledClass bookedClass, Student student) {
		this(bookedClass, new StudentRef(student));
	}

	public PlaceAlreadyTakenBySomeoneElseException(ScheduledClass bookedClass, UnregisteredUser student) {
		this(bookedClass, new StudentRef(student));
	}
}
