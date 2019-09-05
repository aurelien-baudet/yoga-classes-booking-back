package fr.yoga.booking.repository;

import java.util.List;

import fr.yoga.booking.domain.reservation.Lesson;

public interface CustomizedLessonRepository {
	List<Lesson> findAllUnscheduled();
}
