package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.notification.Notification;
import lombok.Getter;

@Getter
public class UnreachableUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Student student;
	private final Notification notification;

	public UnreachableUserException(Student student, Notification notification) {
		super("User "+student.getDisplayName()+" can't be contacted because neither email address nor phone number is provided");
		this.student = student;
		this.notification = notification;
	}

}
