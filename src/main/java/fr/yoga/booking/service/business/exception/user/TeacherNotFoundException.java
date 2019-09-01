package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class TeacherNotFoundException extends UserNotFoundException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TeacherNotFoundException(String userId) {
		super(userId, "Student doesn't exist");
	}

	public TeacherNotFoundException(String userId, String message) {
		super(userId, message);
	}

	public TeacherNotFoundException(String userId, Throwable cause) {
		super(userId, cause);
	}

	public TeacherNotFoundException(String userId, String message, Throwable cause) {
		super(userId, message, cause);
	}

}
