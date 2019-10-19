package fr.yoga.booking.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomizedScheduledClassRepositoryImpl implements CustomizedScheduledClassRepository {
	private final MongoOperations mongo;
	
	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(StudentInfo student) {
		if(student.isRegistered()) {
			return findNextBookedClassesForStudent(student.toStudent());
		}
		return findNextBookedClassesForStudent(student.toUnregisteredUser());
	}
	
	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(Student student) {
		Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
		Criteria where = where("start").gte(today)
				.and("bookings").elemMatch(registeredUserCriteria(student));
		return mongo.find(query(where), ScheduledClass.class);
	}

	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(UnregisteredUser student) {
		Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
		Criteria where = where("start").gte(today)
				.and("bookings").elemMatch(unregisteredUserCriteria(student));
		return mongo.find(query(where), ScheduledClass.class);
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, StudentInfo student) {
		if(student.isRegistered()) {
			return existsBookedClassForStudent(bookedClass, student.toStudent());
		}
		return existsBookedClassForStudent(bookedClass, student.toUnregisteredUser());
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, Student student) {
		Criteria where = where("_id").is(bookedClass.getId())
				.and("bookings").elemMatch(registeredUserCriteria(student));
		return mongo.exists(query(where), ScheduledClass.class);
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, UnregisteredUser student) {
		Criteria where = where("_id").is(bookedClass.getId())
				.and("bookings").elemMatch(unregisteredUserCriteria(student));
		return mongo.exists(query(where), ScheduledClass.class);
	}

	@Override
	public List<ScheduledClass> findByLessonAndStartAfter(Lesson lesson, Optional<Instant> start) {
		Criteria where = where("lesson._id").is(lesson.getId());
		if(start.isPresent()) {
			where = where.and("start").gte(start.get());
		}
		return mongo.find(query(where), ScheduledClass.class);
	}

	@Override
	public List<ScheduledClass> findByLessonAndStartAfterAndEndBefore(Lesson lesson, Optional<Instant> start, Optional<Instant> end) {
		Criteria where = where("lesson._id").is(lesson.getId());
		if(start.isPresent()) {
			where = where.and("start").gte(start.get());
		}
		if(end.isPresent()) {
			where = where.and("end").lte(end.get());
		}
		return mongo.find(query(where), ScheduledClass.class);
	}

	private Criteria registeredUserCriteria(Student student) {
		return where("student._id").is(student.getId());
	}

	private Criteria unregisteredUserCriteria(UnregisteredUser student) {
		return where("student.displayName").is(student.getDisplayName())
				.and("student.email").is(student.getEmail())
				.and("student.phoneNumber").is(student.getPhoneNumber());
	}
}
