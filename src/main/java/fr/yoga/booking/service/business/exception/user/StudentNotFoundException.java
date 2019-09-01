package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class StudentNotFoundException extends UserNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StudentNotFoundException(String userId) {
		super(userId, "Student doesn't exist");
	}

	public StudentNotFoundException(String userId, String message) {
		super(userId, message);
	}

	public StudentNotFoundException(String userId, Throwable cause) {
		super(userId, cause);
	}

	public StudentNotFoundException(String userId, String message, Throwable cause) {
		super(userId, message, cause);
	}

}
