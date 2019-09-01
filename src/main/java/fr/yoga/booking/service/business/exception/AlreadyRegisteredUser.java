package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.service.business.exception.user.AccountException;

public class AlreadyRegisteredUser extends AccountException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyRegisteredUser(User user, String message, Throwable cause) {
		super(user, message, cause);
	}

	public AlreadyRegisteredUser(User user, String message) {
		super(user, message);
	}

	public AlreadyRegisteredUser(User user, Throwable cause) {
		super(user, cause);
	}

	public AlreadyRegisteredUser(User user) {
		super(user, "A user with same login already exists");
	}

}
