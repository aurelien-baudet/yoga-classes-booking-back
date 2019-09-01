package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;

@Getter
public class BookingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final ScheduledClass bookedClass;
	private final StudentInfo student;

	public BookingException(ScheduledClass bookedClass, StudentInfo student, String message) {
		super(message);
		this.bookedClass = bookedClass;
		this.student = student;
	}

	public BookingException(ScheduledClass bookedClass, StudentInfo student, String message, Throwable cause) {
		super(message, cause);
		this.bookedClass = bookedClass;
		this.student = student;
	}

	public BookingException(ScheduledClass bookedClass, Student student, String message) {
		this(bookedClass, new StudentInfo(student), message);
	}

	public BookingException(ScheduledClass bookedClass, Student student, String message, Throwable cause) {
		this(bookedClass, new StudentInfo(student), message, cause);
	}

	public BookingException(ScheduledClass bookedClass, UnregisteredUser student, String message) {
		this(bookedClass, new StudentInfo(student), message);
	}

	public BookingException(ScheduledClass bookedClass, UnregisteredUser student, String message, Throwable cause) {
		this(bookedClass, new StudentInfo(student), message, cause);
	}
}
