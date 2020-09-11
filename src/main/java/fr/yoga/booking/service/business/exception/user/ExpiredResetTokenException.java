package fr.yoga.booking.service.business.exception.user;

import lombok.Getter;

@Getter
public class ExpiredResetTokenException extends PasswordResetException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String token;
	
	public ExpiredResetTokenException(String token) {
		this("Token expired", token);
	}
	
	public ExpiredResetTokenException(String message, String token) {
		super(message);
		this.token = token;
	}
	
}
