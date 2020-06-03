package fr.yoga.booking.service.business.exception.reservation;

import static fr.yoga.booking.util.DateRangeUtil.format;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public class AlreadyBookedException extends BookingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyBookedException(ScheduledClass bookedClass, StudentRef student) {
		super(bookedClass, student, student.getDisplayName() + " is already registered to class " + format(bookedClass.getStart(), bookedClass.getEnd()));
	}

	public AlreadyBookedException(ScheduledClass bookedClass, Student student) {
		this(bookedClass, new StudentRef(student));
	}

	public AlreadyBookedException(ScheduledClass bookedClass, UnregisteredUser student) {
		this(bookedClass, new StudentRef(student));
	}
}
