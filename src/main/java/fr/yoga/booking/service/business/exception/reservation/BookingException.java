package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;

@Getter
public class BookingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ScheduledClass bookedClass;
	private final StudentRef student;

	public BookingException(ScheduledClass bookedClass, StudentRef student, String message) {
		super(message);
		this.bookedClass = bookedClass;
		this.student = student;
	}

	public BookingException(ScheduledClass bookedClass, StudentRef student, String message, Throwable cause) {
		super(message, cause);
		this.bookedClass = bookedClass;
		this.student = student;
	}

	public BookingException(ScheduledClass bookedClass, Student student, String message) {
		this(bookedClass, new StudentRef(student), message);
	}

	public BookingException(ScheduledClass bookedClass, Student student, String message, Throwable cause) {
		this(bookedClass, new StudentRef(student), message, cause);
	}

	public BookingException(ScheduledClass bookedClass, UnregisteredUser student, String message) {
		this(bookedClass, new StudentRef(student), message);
	}

	public BookingException(ScheduledClass bookedClass, UnregisteredUser student, String message, Throwable cause) {
		this(bookedClass, new StudentRef(student), message, cause);
	}
}
