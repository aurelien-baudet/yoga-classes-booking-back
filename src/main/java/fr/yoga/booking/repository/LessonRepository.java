package fr.yoga.booking.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.reservation.Lesson;

public interface LessonRepository extends MongoRepository<Lesson, String>, CustomizedLessonRepository {

	List<Lesson> findByRemovedIsNullOrRemovedFalse();

}
