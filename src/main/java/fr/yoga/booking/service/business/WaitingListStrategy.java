package fr.yoga.booking.service.business;

import fr.yoga.booking.domain.reservation.ScheduledClass;

public interface WaitingListStrategy {
	ScheduledClass placeFreed(ScheduledClass scheduledClass);

}
