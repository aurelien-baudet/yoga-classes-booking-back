package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends UserException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String userId;

	public UserNotFoundException(String userId) {
		super("User doesn't exist");
		this.userId = userId;
	}

	public UserNotFoundException(String userId, String message) {
		super(message);
		this.userId = userId;
	}

	public UserNotFoundException(String userId, Throwable cause) {
		super(cause);
		this.userId = userId;
	}

	public UserNotFoundException(String userId, String message, Throwable cause) {
		super(message, cause);
		this.userId = userId;
	}

}
