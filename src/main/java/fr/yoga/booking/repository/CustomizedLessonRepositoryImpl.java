package fr.yoga.booking.repository;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomizedLessonRepositoryImpl implements CustomizedLessonRepository {
	private final MongoOperations mongo;
	

	@Override
	public List<Lesson> findAllUnscheduledAndRemovedFalse() {
		List<String> scheduledIds = mongo.findDistinct("lesson._id", ScheduledClass.class, ObjectId.class)
				.stream()
				.map(ObjectId::toHexString)
				.collect(toList());
		Criteria where = where("_id").nin(scheduledIds).and("removed").ne(true);
		return mongo.find(query(where), Lesson.class);
	}


}
