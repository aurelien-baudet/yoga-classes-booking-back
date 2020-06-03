package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Getter;

@Getter
public class UnreachableUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final StudentRef student;
	private final Notification notification;

	public UnreachableUserException(StudentRef student, Notification notification) {
		super("User "+student.getDisplayName()+" can't be contacted because neither email address nor phone number is provided");
		this.student = student;
		this.notification = notification;
	}

}
