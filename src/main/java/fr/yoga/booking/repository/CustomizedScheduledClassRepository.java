package fr.yoga.booking.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public interface CustomizedScheduledClassRepository {
	List<ScheduledClass> findNextBookedClassesForStudent(StudentInfo student);
	List<ScheduledClass> findNextBookedClassesForStudent(Student student);
	List<ScheduledClass> findNextBookedClassesForStudent(UnregisteredUser student);

	boolean existsBookedClassForStudent(ScheduledClass bookedClass, StudentInfo student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, Student student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, UnregisteredUser student);

	List<ScheduledClass> findByLessonAndStartAfter(Lesson lesson, Optional<Instant> start);
	List<ScheduledClass> findByLessonAndStartAfterAndEndBefore(Lesson lesson, Optional<Instant> start, Optional<Instant> end);
}
