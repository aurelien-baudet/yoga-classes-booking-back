package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.notification.Reminder;
import lombok.Getter;

@Getter
public class RemindBookingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Reminder<?> reminder;

	public RemindBookingException(Reminder<?> reminder, String message, Throwable cause) {
		super(message, cause);
		this.reminder = reminder;
	}

}
