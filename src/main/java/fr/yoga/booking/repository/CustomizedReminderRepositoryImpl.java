package fr.yoga.booking.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.notification.Reminder;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomizedReminderRepositoryImpl implements CustomizedReminderRepository {
	private final MongoOperations mongo;

	@Override
	public List<Reminder> findByRemindAtBetween(Instant after, Instant before) {
		Criteria where = where("remindAt").elemMatch(where("").gte(after).lt(before));
		return mongo.find(query(where), Reminder.class);
	}

	@Override
	public List<Reminder> findByRemindAtBefore(Instant date) {
		Criteria where = where("remindAt").elemMatch(where("").lt(date));
		return mongo.find(query(where), Reminder.class);
	}

}
