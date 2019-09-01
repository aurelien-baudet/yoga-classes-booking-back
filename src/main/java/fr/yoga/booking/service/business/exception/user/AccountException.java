package fr.yoga.booking.service.business.exception.user;

import fr.yoga.booking.domain.account.User;
import lombok.Getter;

@Getter
public class AccountException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final User user;

	public AccountException(User user, String message) {
		super(message);
		this.user = user;
	}

	public AccountException(User user, Throwable cause) {
		super(cause);
		this.user = user;
	}

	public AccountException(User user, String message, Throwable cause) {
		super(message, cause);
		this.user = user;
	}
	
}
