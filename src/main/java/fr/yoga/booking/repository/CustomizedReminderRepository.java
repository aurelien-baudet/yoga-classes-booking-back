package fr.yoga.booking.repository;

import java.time.Instant;
import java.util.List;

import fr.yoga.booking.domain.notification.Reminder;

public interface CustomizedReminderRepository {
	List<Reminder<?>> findByRemindAtBefore(Instant date);
	List<Reminder<?>> findByRemindAtBetween(Instant after, Instant before);
}
