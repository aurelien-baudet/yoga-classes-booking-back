package fr.yoga.booking.service.business.exception.user;

public class PasswordResetException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordResetException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordResetException(String message) {
		super(message);
	}

	public PasswordResetException(Throwable cause) {
		super(cause);
	}

}
