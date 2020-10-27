package fr.yoga.booking.service.technical.scheduling;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TriggeredRepository extends MongoRepository<Triggered, String> {
	boolean existsByTriggerId(String id);
	
	Triggered findFirstByTriggerType(String type, Sort by);
}
