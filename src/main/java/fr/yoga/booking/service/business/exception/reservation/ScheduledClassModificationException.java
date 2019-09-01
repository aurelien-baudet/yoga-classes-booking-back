package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;

@Getter
public class ScheduledClassModificationException extends ScheduledClassException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ScheduledClass scheduledClass;
	
	public ScheduledClassModificationException(ScheduledClass scheduledClass, String message, Throwable cause) {
		super(message, cause);
		this.scheduledClass = scheduledClass;
	}

}
