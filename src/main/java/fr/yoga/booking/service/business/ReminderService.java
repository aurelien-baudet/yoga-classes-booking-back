package fr.yoga.booking.service.business;

import static java.time.Instant.now;
import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Order.asc;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.joda.time.Instant;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.notification.Reminded;
import fr.yoga.booking.domain.notification.Reminder;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.RemindedRepository;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.reservation.RemindBookingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
	private final ScheduledClassRepository classesRepository;
	private final RemindedRepository remindedRepository;
	private final BookingService bookingService;
	private final ReminderProperties reminderProperties;

//	public void registerReminderForClass(ScheduledClass savedClass) {
//		reminderRepository.save(new Reminder(savedClass, reminderProperties.getNextClass()
//				.stream()
//				.map(time -> savedClass.getStart().minus(time))
//				.collect(toList())));
//	}
//
//	public void unregisterReminderForClass(ScheduledClass updated) {
//		reminderRepository.deleteByScheduledClassId(updated.getId());
//	}
	
	
	@Scheduled(fixedRateString="PT1M")
	private void checkClassesToRemind() {
		trigger(findClassesToRemind());
	}
	
	@Scheduled(fixedRateString="${reminder.clean-interval:P1D}")
	private void cleanOldReminders() {
		remindedRepository.deleteAllByScheduledClassStartDateBefore(Instant.now());
	}
	
	private List<Reminder> findClassesToRemind() {
		Duration firstReminderInTime = reminderProperties.getNextClass().last();
		if (firstReminderInTime == null) {
			return emptyList();
		}
		// find classes that are not already started
		List<ScheduledClass> futureClasses = classesRepository.findByStartAfter(now(), Sort.by(asc("start")));
		// start date is after the greater reminder date in the past
		// keep only those that have not already been triggered
		return toRemindersThatNeedToBeTriggered(futureClasses);
	}



	private void trigger(List<Reminder> reminders) {
		for (Reminder reminder : reminders) {
			log.info("Trigger reminder {} before start of class {} ({})", reminder.getReminder(), reminder.getScheduledClass().getId(), reminder.getScheduledClass().getStart());
			try {
				bookingService.remindStudentsAboutNextClass(reminder);
				markAsTriggered(reminder);
			} catch (RemindBookingException e) {
				log.warn("Failed to remind students about next class", e);
				// TODO: Should handle properly this error
			}
		}
	}

	private void markAsTriggered(Reminder reminder) {
		remindedRepository.save(new Reminded(reminder));
		// also save reminders that are past
		for (Duration d : reminderProperties.getNextClass()) {
			if (isPastReminder(reminder, d) && !isAlreadyMarkedAsTriggered(reminder, d)) {
				log.info("Also mark reminder {} triggered for {} ({})", d, reminder.getScheduledClass().getId(), reminder.getScheduledClass().getStart());
				remindedRepository.save(new Reminded(new Reminder(reminder.getScheduledClass(), d)));
			}
		}
	}

	private boolean isPastReminder(Reminder reminder, Duration d) {
		return d.compareTo(reminder.getReminder()) > 0;
	}

	private boolean isAlreadyMarkedAsTriggered(Reminder reminder, Duration d) {
		return remindedRepository.existsByScheduledClassIdAndReminder(reminder.getScheduledClass().getId(), d);
	}

	private boolean alreadyReminded(Reminder reminder) {
		return remindedRepository.existsByScheduledClassIdAndReminder(reminder.getScheduledClass().getId(), reminder.getReminder());
	}
	
	private List<Reminder> toRemindersThatNeedToBeTriggered(List<ScheduledClass> classes) {
		return classes.stream()
				.map(this::keepNotTriggered)
				.filter(Objects::nonNull)
				.collect(toList());
	}
	
	private Reminder keepNotTriggered(ScheduledClass scheduledClass) {
		return reminderProperties.getNextClass().stream()
				.map(r -> new Reminder(scheduledClass, r))
				.filter(Reminder::isNowBetweenReminderAndStartOfClass)
				.filter(not(this::alreadyReminded))
				.findFirst()
				.orElse(null);
	}
	
	
//	@Scheduled(fixedRateString="PT1M")
//	private void checkClassesToRemind() {
//		try {
//			Instant before = now().plus(1, MINUTES);
//			List<Reminder> reminders = reminderRepository.findByRemindAtBefore(before);
//			for(Reminder reminder : reminders) {
//				bookingService.remindStudentsAboutNextClass(reminder);
//				List<Instant> updatedRemindAt = removeMatchingRemindAtDates(before, reminder);
//				reminder.setRemindAt(updatedRemindAt);
//				// remove if no more reminder date
//				// update if there remains dates
//				if(updatedRemindAt.isEmpty()) {
//					reminderRepository.delete(reminder);
//				} else {
//					reminder.setRemindAt(updatedRemindAt);
//					reminderRepository.save(reminder);
//				}
//			}
//		} catch(RemindBookingException e) {
//			log.warn("Failed to remind students about next class", e);
//			// TODO: Should handle properly this error
//		}
//	}
//
//	private List<Instant> removeMatchingRemindAtDates(Instant before, Reminder reminder) {
//		List<Instant> updatedRemindAt = reminder.getRemindAt()
//			.stream()
//			.dropWhile(i -> i.isBefore(before))
//			.collect(toList());
//		return updatedRemindAt;
//	}
}
