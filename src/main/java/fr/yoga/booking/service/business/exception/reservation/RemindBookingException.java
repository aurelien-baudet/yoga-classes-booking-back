package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.service.technical.scheduling.Trigger;
import lombok.Getter;

@Getter
public class RemindBookingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Trigger<?> reminder;

	public RemindBookingException(Trigger<?> reminder, String message, Throwable cause) {
		super(message, cause);
		this.reminder = reminder;
	}

}
