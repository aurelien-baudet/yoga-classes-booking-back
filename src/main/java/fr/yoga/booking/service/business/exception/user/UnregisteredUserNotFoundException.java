package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class UnregisteredUserNotFoundException extends UserNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnregisteredUserNotFoundException(String userId) {
		super(userId, "Unregistered user doesn't exist");
	}

	public UnregisteredUserNotFoundException(String userId, String message) {
		super(userId, message);
	}

	public UnregisteredUserNotFoundException(String userId, Throwable cause) {
		super(userId, cause);
	}

	public UnregisteredUserNotFoundException(String userId, String message, Throwable cause) {
		super(userId, message, cause);
	}

}
