package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class InvalidResetTokenException extends PasswordResetException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String token;
	
	public InvalidResetTokenException(String token) {
		this("Invalid token", token);
	}
	
	public InvalidResetTokenException(String message, String token) {
		super(message);
		this.token = token;
	}
	
}
