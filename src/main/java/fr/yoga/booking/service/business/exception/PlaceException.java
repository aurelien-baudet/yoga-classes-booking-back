package fr.yoga.booking.service.business.exception;

public class PlaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlaceException(String message) {
		super(message);
	}

	public PlaceException(Throwable cause) {
		super(cause);
	}

	public PlaceException(String message, Throwable cause) {
		super(message, cause);
	}

}
