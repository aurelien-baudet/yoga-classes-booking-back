package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Getter;

@Getter
public class CantRemoveClassWithBookersException extends ScheduledClassException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final ScheduledClass scheduledClass;
	
	
	public CantRemoveClassWithBookersException(ScheduledClass scheduledClass) {
		super("The class can't " + scheduledClass.getId() + " be removed since some students have booked for it");
		this.scheduledClass = scheduledClass;
	}

}
