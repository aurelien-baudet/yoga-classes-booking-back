package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.notification.Reminder;

public interface ReminderRepository extends MongoRepository<Reminder, String>, CustomizedReminderRepository {

	void deleteByScheduledClassId(String id);

}
