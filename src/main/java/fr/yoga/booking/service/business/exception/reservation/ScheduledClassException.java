package fr.yoga.booking.service.business.exception.reservation;

public class ScheduledClassException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScheduledClassException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScheduledClassException(String message) {
		super(message);
	}

	public ScheduledClassException(Throwable cause) {
		super(cause);
	}

}
