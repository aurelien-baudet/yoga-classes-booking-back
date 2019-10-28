package fr.yoga.booking.service.business.exception.reservation;

import static fr.yoga.booking.util.DateRangeUtil.format;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public class NotBookedException extends BookingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotBookedException(ScheduledClass bookedClass, StudentInfo student) {
		super(bookedClass, student, "Can't cancel booking for " + student.getDisplayName() + " because student is not registered to class " + format(bookedClass.getStart(), bookedClass.getEnd()));
	}

	public NotBookedException(ScheduledClass bookedClass, Student student) {
		this(bookedClass, new StudentInfo(student));
	}

	public NotBookedException(ScheduledClass bookedClass, UnregisteredUser student) {
		this(bookedClass, new StudentInfo(student));
	}
}
