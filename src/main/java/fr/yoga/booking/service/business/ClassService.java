package fr.yoga.booking.service.business;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.domain.Sort.Order.asc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Canceled;
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
	private final ReminderService reminderService;

	public Lesson register(LessonInfo data, Place place, Teacher teacher) throws ScheduledClassException {
		Lesson lesson = new Lesson(data, place, teacher);
		return lessonRepository.save(lesson);
	}
	
	public ScheduledClass schedule(Lesson lesson, Instant start, Instant end) throws ScheduledClassException {
		ScheduledClass scheduledClass = new ScheduledClass(start, end, lesson);
		ScheduledClass savedClass = scheduledClassRepository.save(scheduledClass);
		reminderService.registerReminderForClass(savedClass);
		return savedClass;
	}
	
	public ScheduledClass cancel(ScheduledClass scheduledClass, CancelData addtionalInfo) throws ScheduledClassException {
		// update class
		scheduledClass.setState(new Canceled(addtionalInfo.getMessage()));
		ScheduledClass updated = scheduledClassRepository.save(scheduledClass);
		reminderService.unregisterReminderForClass(updated);
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
		Lesson lesson = scheduledClass.getLesson();
		Place oldPlace = lesson.getPlace();
		// update class
		lesson.setPlace(newPlace);
		lesson.setPlaceChanged(true);
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

	public ScheduledClass updateLessonInfoForSpecificClass(ScheduledClass scheduledClass, LessonInfo newInfo) {
		scheduledClass.getLesson().setInfo(newInfo);
		// TODO: if max participants has changed => update lists of participants
		return scheduledClassRepository.save(scheduledClass);
	}

	public Lesson updateLessonForAllClasses(Lesson lesson, LessonInfo newInfo) {
		lesson.setInfo(newInfo);
		Lesson updated = lessonRepository.save(lesson);
		List<ScheduledClass> classesForLesson = scheduledClassRepository.findByLessonAndStartAfter(updated, Optional.of(Instant.now()));
		for(ScheduledClass classForLesson : classesForLesson) {
			classForLesson.setLesson(updated);
			scheduledClassRepository.save(classForLesson);
		}
		return updated;
	}
}
