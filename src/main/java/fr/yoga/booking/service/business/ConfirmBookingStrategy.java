package fr.yoga.booking.service.business;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.exception.reservation.BookingException;

public interface ConfirmBookingStrategy {
	ScheduledClass confirm(ScheduledClass scheduledClass, StudentRef student, User bookedBy) throws BookingException;
}
