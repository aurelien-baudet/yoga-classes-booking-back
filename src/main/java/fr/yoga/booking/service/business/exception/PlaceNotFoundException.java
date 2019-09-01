package fr.yoga.booking.service.business.exception;

import lombok.Getter;

@Getter
public class PlaceNotFoundException extends PlaceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String placeId;

	public PlaceNotFoundException(String placeId) {
		this(placeId, "Place doesn't exist");
	}

	public PlaceNotFoundException(String placeId, String message) {
		super(message);
		this.placeId = placeId;
	}

	public PlaceNotFoundException(String placeId, Throwable cause) {
		super(cause);
		this.placeId = placeId;
	}

	public PlaceNotFoundException(String placeId, String message, Throwable cause) {
		super(message, cause);
		this.placeId = placeId;
	}

}
