package fr.yoga.booking.service.technical.event;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.Data;

@Data
public class ClassStarted {
	private final ScheduledClass scheduledClass;
}
