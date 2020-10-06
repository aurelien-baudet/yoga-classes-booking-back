package fr.yoga.booking.repository;

import java.time.Duration;

import org.joda.time.Instant;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.notification.Reminded;

public interface RemindedRepository extends MongoRepository<Reminded, String> {
	void deleteByScheduledClassId(String id);
	boolean existsByScheduledClassIdAndReminder(String scheduledClass, Duration reminder);
	void deleteAllByScheduledClassStartDateBefore(Instant now);
}
