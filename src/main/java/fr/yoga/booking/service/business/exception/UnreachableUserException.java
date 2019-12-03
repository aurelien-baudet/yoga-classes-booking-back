package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.domain.notification.PushNotification;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Getter;

@Getter
public class UnreachableUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final StudentInfo student;
	private final PushNotification notification;

	public UnreachableUserException(StudentInfo student, PushNotification notification) {
		super("User "+student.getDisplayName()+" can't be contacted because neither email address nor phone number is provided");
		this.student = student;
		this.notification = notification;
	}

}
