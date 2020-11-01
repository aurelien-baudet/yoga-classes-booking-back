package fr.yoga.booking.service.business;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.domain.Sort.Order.asc;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
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
import fr.yoga.booking.service.business.security.annotation.CanCancelClass;
import fr.yoga.booking.service.business.security.annotation.CanChangeAllPlaces;
import fr.yoga.booking.service.business.security.annotation.CanChangeAllTeachers;
import fr.yoga.booking.service.business.security.annotation.CanChangePlace;
import fr.yoga.booking.service.business.security.annotation.CanChangeTeacher;
import fr.yoga.booking.service.business.security.annotation.CanListClassesForLesson;
import fr.yoga.booking.service.business.security.annotation.CanListFutureClasses;
import fr.yoga.booking.service.business.security.annotation.CanListLessons;
import fr.yoga.booking.service.business.security.annotation.CanListUnscheduledLessons;
import fr.yoga.booking.service.business.security.annotation.CanRegisterLesson;
import fr.yoga.booking.service.business.security.annotation.CanScheduleClass;
import fr.yoga.booking.service.business.security.annotation.CanUpdateLessonInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewClassInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewLessonInfo;
import fr.yoga.booking.service.technical.event.ClassEnded;
import fr.yoga.booking.service.technical.event.ClassStarted;
import fr.yoga.booking.service.technical.scheduling.SchedulingHelper;
import fr.yoga.booking.service.technical.scheduling.Trigger;
import fr.yoga.booking.service.technical.scheduling.Triggered;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassService {
	private final ScheduledClassRepository scheduledClassRepository;
	private final LessonRepository lessonRepository;
	private final NotificationService notificationService;
	private final SchedulingHelper scheduler;
	private final ApplicationEventPublisher eventPublisher;
	private final SchedulingProperties schedulingProperties;

	@CanRegisterLesson
	public Lesson register(LessonInfo data, Place place, Teacher teacher) throws ScheduledClassException {
		Lesson lesson = new Lesson(data, place, teacher);
		return lessonRepository.save(lesson);
	}
	
	@CanScheduleClass
	public ScheduledClass schedule(Lesson lesson, Instant start, Instant end) throws ScheduledClassException {
		ScheduledClass scheduledClass = new ScheduledClass(start, end, lesson);
		return scheduleEvents(scheduledClassRepository.save(scheduledClass));
	}
	
	@EventListener
	public void restoreTriggersAfterReboot(ApplicationStartedEvent event) {
		for (ScheduledClass scheduledClass : findFutureClassesOrPastButNeverTriggered()) {
			scheduleEvents(scheduledClass);
		}
	}

	@CanCancelClass
	public ScheduledClass cancel(ScheduledClass scheduledClass, CancelData addtionalInfo) throws ScheduledClassException {
		// update class
		scheduledClass.setState(new Canceled(addtionalInfo.getMessage()));
		ScheduledClass updated = scheduledClassRepository.save(scheduledClass);
		// notify every participant
		notificationService.classCanceled(updated, addtionalInfo);
		return updated;
	}
	
	@CanViewLessonInfo
	public Lesson getLesson(String lessonId) throws ScheduledClassException {
		return lessonRepository.findById(lessonId)
				.orElseThrow(() -> new LessonNotFoundException(lessonId));
	}
	
	@CanViewClassInfo
	public ScheduledClass getClass(String classId) throws ScheduledClassException {
		return scheduledClassRepository.findById(classId)
				.orElseThrow(() -> new ScheduledClassNotFoundException(classId));
	}
	
	@CanListFutureClasses
	public List<ScheduledClass> listFutureClasses() {
		Instant today = Instant.now().truncatedTo(DAYS);
		return scheduledClassRepository.findByStartAfter(today, Sort.by(asc("start")));
	}
	
	@CanChangePlace
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

	@CanChangeTeacher
	public ScheduledClass changeTeacher(ScheduledClass scheduledClass, Teacher teacher) {
		Lesson lesson = scheduledClass.getLesson();
		lesson.setTeacher(teacher);
		return scheduledClassRepository.save(scheduledClass);
	}

	@CanListClassesForLesson
	public List<ScheduledClass> listClassesFor(Lesson lesson, Instant from, Instant to) {
		return scheduledClassRepository.findByLessonAndStartAfterAndEndBefore(lesson, Optional.ofNullable(from), Optional.ofNullable(to));
	}

	@CanListLessons
	public List<Lesson> listLessons() {
		return lessonRepository.findAll();
	}

	@CanListUnscheduledLessons
	public List<Lesson> listUnscheduledLessons() {
		return lessonRepository.findAllUnscheduled();
	}

	@CanUpdateLessonInfo
	public ScheduledClass updateLessonInfoForSpecificClass(ScheduledClass scheduledClass, LessonInfo newInfo) {
		scheduledClass.getLesson().setInfo(newInfo);
		// TODO: if max participants has changed => update lists of participants
		return scheduledClassRepository.save(scheduledClass);
	}

	@CanUpdateLessonInfo
	public Lesson updateLessonForAllClasses(Lesson lesson, LessonInfo newInfo) throws ScheduledClassException {
		lesson.setInfo(newInfo);
		Lesson updated = lessonRepository.save(lesson);
		List<ScheduledClass> classesForLesson = scheduledClassRepository.findByLessonAndStartAfter(updated, Optional.of(Instant.now()));
		for(ScheduledClass classForLesson : classesForLesson) {
			classForLesson.setLesson(updated);
			scheduledClassRepository.save(classForLesson);
		}
		return updated;
	}

	@CanChangeAllPlaces
	public Lesson updatePlaceForAllClasses(Lesson lesson, Place newPlace) throws ScheduledClassException {
		lesson.setPlace(newPlace);
		lesson.setPlaceChanged(true);
		Lesson updated = lessonRepository.save(lesson);
		List<ScheduledClass> classesForLesson = scheduledClassRepository.findByLessonAndStartAfter(updated, Optional.of(Instant.now()));
		for(ScheduledClass classForLesson : classesForLesson) {
			changePlace(classForLesson, newPlace);
		}
		return updated;
	}
	
	@CanChangeAllTeachers
	public Lesson updateTeacherForAllClasses(Lesson lesson, Teacher teacher) throws ScheduledClassException {
		lesson.setTeacher(teacher);
		Lesson updated = lessonRepository.save(lesson);
		List<ScheduledClass> classesForLesson = scheduledClassRepository.findByLessonAndStartAfter(updated, Optional.of(Instant.now()));
		for(ScheduledClass classForLesson : classesForLesson) {
			changeTeacher(classForLesson, teacher);
		}
		return updated;
	}
	
	private ScheduledClass scheduleEvents(ScheduledClass scheduledClass) {
		if (!schedulingProperties.isEnableClassEvents()) {
			return scheduledClass;
		}
		scheduler.scheduleOnce(new Trigger<>("class-start|"+scheduledClass.getId()+"|"+scheduledClass.getStart(), 
				scheduledClass, 
				"class-start",
				scheduledClass.getStart(), 
				triggerStartEvent(scheduledClass)));
		scheduler.scheduleOnce(new Trigger<>("class-end|"+scheduledClass.getId()+"|"+scheduledClass.getEnd(), 
				scheduledClass,
				"class-end",
				scheduledClass.getEnd(), 
				triggerEndEvent(scheduledClass)));
		return scheduledClass;
	}

	
	private Set<ScheduledClass> findFutureClassesOrPastButNeverTriggered() {
		Triggered latestStartTriggered = scheduler.getLatestTriggeredForType("class-start");
		Triggered latestEndTriggered = scheduler.getLatestTriggeredForType("class-end");
		Set<ScheduledClass> merged = new HashSet<>();
		addScheduledClassesAfter(latestStartTriggered, merged);
		addScheduledClassesAfter(latestEndTriggered, merged);
		List<ScheduledClass> futureClasses = scheduledClassRepository.findByStartAfter(now(), Sort.by(asc("start")));
		merged.addAll(futureClasses);
		return merged;
	}

	private void addScheduledClassesAfter(Triggered triggered, Set<ScheduledClass> merged) {
		if (triggered == null) {
			return;
		}
		ScheduledClass associatedClass = scheduledClassRepository.findById(triggered.getContextId())
				.orElse(null);
		if (associatedClass == null) {
			return;
		}
		merged.addAll(scheduledClassRepository.findByStartAfter(associatedClass.getStart(), Sort.by(asc("start"))));
	}

	private Runnable triggerStartEvent(ScheduledClass scheduledClass) {
		return () -> {
			log.debug("Trigger start event for class '{}' started at {} [{}]", scheduledClass.getLesson().getInfo().getTitle(), scheduledClass.getStart(), scheduledClass.getId());
			ScheduledClass uptodate = scheduledClassRepository.findById(scheduledClass.getId())
					.orElseThrow(() -> new IllegalStateException("Invalid class "+scheduledClass.getId()));
			eventPublisher.publishEvent(new ClassStarted(uptodate));
		};
	}
	
	private Runnable triggerEndEvent(ScheduledClass scheduledClass) {
		return () -> {
			log.debug("Trigger end event for class '{}' ended at {} [{}]", scheduledClass.getLesson().getInfo().getTitle(), scheduledClass.getEnd(), scheduledClass.getId());
			ScheduledClass uptodate = scheduledClassRepository.findById(scheduledClass.getId())
					.orElseThrow(() -> new IllegalStateException("Invalid class "+scheduledClass.getId()));
			eventPublisher.publishEvent(new ClassEnded(uptodate));
		};
	}
}
