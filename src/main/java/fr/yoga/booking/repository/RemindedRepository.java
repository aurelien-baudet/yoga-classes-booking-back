package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.notification.Reminded;

public interface RemindedRepository extends MongoRepository<Reminded, String> {
	boolean existsByReminderId(String id);
}
