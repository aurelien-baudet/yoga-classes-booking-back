package fr.yoga.booking.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.reservation.ScheduledClass;

public interface ScheduledClassRepository extends MongoRepository<ScheduledClass, String>, CustomizedScheduledClassRepository {
	List<ScheduledClass> findByStartAfter(Instant date, Sort sortBy);

	List<ScheduledClass> findByStartBetween(Instant before, Instant after);
}
