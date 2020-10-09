package fr.yoga.booking.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;

public interface CustomizedScheduledClassRepository {
	List<ScheduledClass> findNextBookedClassesForStudent(StudentRef student);
	List<ScheduledClass> findNextBookedClassesForStudent(Student student);
	List<ScheduledClass> findNextBookedClassesForStudent(UnregisteredUser student);

	ScheduledClass findNextBookedClassForStudent(StudentRef student);
	ScheduledClass findNextBookedClassForStudent(Student student);
	ScheduledClass findNextBookedClassForStudent(UnregisteredUser student);

	boolean existsBookedClassForStudent(ScheduledClass bookedClass, StudentRef student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, Student student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, UnregisteredUser student);

	List<ScheduledClass> findByLessonAndStartAfter(Lesson lesson, Optional<Instant> start);
	List<ScheduledClass> findByLessonAndStartAfterAndEndBefore(Lesson lesson, Optional<Instant> start, Optional<Instant> end);
}
