package fr.yoga.booking.repository;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomizedScheduledClassRepositoryImpl implements CustomizedScheduledClassRepository {
	private final MongoOperations mongo;
	
	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(StudentRef student) {
		Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
		Criteria where = where("start").gte(today)
				.and("bookings").elemMatch(userCriteria(student))
				.and("removed").ne(true);
		return mongo.find(query(where), ScheduledClass.class);
	}
	
	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(Student student) {
		return findNextBookedClassesForStudent(new StudentRef(student));
	}

	@Override
	public List<ScheduledClass> findNextBookedClassesForStudent(UnregisteredUser student) {
		return findNextBookedClassesForStudent(new StudentRef(student));
	}

	@Override
	public ScheduledClass findNextBookedClassForStudent(StudentRef student) {
		Criteria where = where("start").gte(Instant.now())
				.and("bookings").elemMatch(userCriteria(student))
				.and("removed").ne(true);
		return mongo.findOne(query(where).with(Sort.by(asc("start"))).limit(1), ScheduledClass.class);
	}

	@Override
	public ScheduledClass findNextBookedClassForStudent(Student student) {
		return findNextBookedClassForStudent(new StudentRef(student));
	}

	@Override
	public ScheduledClass findNextBookedClassForStudent(UnregisteredUser student) {
		return findNextBookedClassForStudent(new StudentRef(student));
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, StudentRef student) {
		Criteria where = where("_id").is(bookedClass.getId())
				.and("bookings").elemMatch(userCriteria(student))
				.and("removed").ne(true);
		return mongo.exists(query(where), ScheduledClass.class);
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, Student student) {
		return existsBookedClassForStudent(bookedClass, new StudentRef(student));
	}

	@Override
	public boolean existsBookedClassForStudent(ScheduledClass bookedClass, UnregisteredUser student) {
		return existsBookedClassForStudent(bookedClass, new StudentRef(student));
	}

	@Override
	public List<ScheduledClass> findByLessonAndStartAfter(Lesson lesson, Optional<Instant> start) {
		Criteria where = where("lesson._id").is(lesson.getId())
				.and("removed").ne(true);
		if(start.isPresent()) {
			where = where.and("start").gte(start.get());
		}
		return mongo.find(query(where), ScheduledClass.class);
	}

	@Override
	public List<ScheduledClass> findByLessonAndStartAfterAndEndBefore(Lesson lesson, Optional<Instant> start, Optional<Instant> end) {
		Criteria where = where("lesson._id").is(lesson.getId())
				.and("removed").ne(true);
		if(start.isPresent()) {
			where = where.and("start").gte(start.get());
		}
		if(end.isPresent()) {
			where = where.and("end").lte(end.get());
		}
		return mongo.find(query(where), ScheduledClass.class);
	}

	private Criteria userCriteria(StudentRef student) {
		return where("student._id").is(student.getId());
	}

}
