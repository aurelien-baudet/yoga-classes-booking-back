package fr.yoga.booking.service.business;

import static fr.yoga.booking.domain.reservation.ClassState.CANCELED;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.domain.Sort.Order.asc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.LessonRepository;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.LessonNotFoundException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassService {
	private final ScheduledClassRepository scheduledClassRepository;
	private final LessonRepository lessonRepository;
	private final NotificationService notificationService;

	public Lesson register(LessonInfo data, Place place, Teacher teacher) throws ScheduledClassException {
		Lesson lesson = new Lesson(data, place, teacher);
		return lessonRepository.save(lesson);
	}
	
	public ScheduledClass schedule(Lesson lesson, Instant start, Instant end) throws ScheduledClassException {
		ScheduledClass scheduledClass = new ScheduledClass(start, end, lesson);
		return scheduledClassRepository.save(scheduledClass);
	}
	
	public ScheduledClass cancel(ScheduledClass scheduledClass, CancelData addtionalInfo) throws ScheduledClassException {
		// update class
		scheduledClass.setState(CANCELED);
		ScheduledClass updated = scheduledClassRepository.save(scheduledClass);
		// notify every participant
		notificationService.classCanceled(updated, addtionalInfo);
		return updated;
	}
	
	public Lesson getLesson(String lessonId) throws ScheduledClassException {
		return lessonRepository.findById(lessonId)
				.orElseThrow(() -> new LessonNotFoundException(lessonId));
	}
	
	public ScheduledClass getClass(String classId) throws ScheduledClassException {
		return scheduledClassRepository.findById(classId)
				.orElseThrow(() -> new ScheduledClassNotFoundException(classId));
	}
	
	public List<ScheduledClass> listFutureClasses() {
		Instant today = Instant.now().truncatedTo(DAYS);
		return scheduledClassRepository.findByStartAfter(today, Sort.by(asc("start")));
	}
	
	public ScheduledClass changePlace(ScheduledClass scheduledClass, Place newPlace) throws ScheduledClassException {
		Place oldPlace = scheduledClass.getLesson().getPlace();
		// update class
		scheduledClass.getLesson().setPlace(newPlace);
		ScheduledClass updated = scheduledClassRepository.save(scheduledClass);
		// notify every participant
		notificationService.placeChanged(updated, oldPlace, newPlace);
		return updated;
	}

	public List<ScheduledClass> listClassesFor(Lesson lesson, Instant from, Instant to) {
		return scheduledClassRepository.findByLessonAndStartAfterAndEndBefore(lesson, Optional.ofNullable(from), Optional.ofNullable(to));
	}

	public List<Lesson> listLessons() {
		return lessonRepository.findAll();
	}

	public List<Lesson> listUnscheduledLessons() {
		return lessonRepository.findAllUnscheduled();
	}
}
