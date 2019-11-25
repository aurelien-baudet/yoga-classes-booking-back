package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.domain.notification.PushNotification;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UnreachableUserException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final StudentInfo student;
	private final PushNotification notification;
}
