package fr.yoga.booking.service.business;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.notification.Reminder;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.ReminderRepository;
import fr.yoga.booking.service.business.exception.reservation.RemindBookingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
	private final ReminderRepository reminderRepository;
	private final BookingService bookingService;

	public void registerReminderForClass(ScheduledClass savedClass) {
		reminderRepository.save(new Reminder(savedClass,
//				savedClass.getStart().minus(1, HOURS), 
				savedClass.getStart().minus(2, HOURS)));
	}

	public void unregisterReminderForClass(ScheduledClass updated) {
		reminderRepository.deleteByScheduledClassId(updated.getId());
	}
	
	@Scheduled(fixedRateString="PT1M")
	private void checkClassesToRemind() {
		try {
			Instant before = now().plus(1, MINUTES);
			List<Reminder> reminders = reminderRepository.findByRemindAtBefore(before);
			for(Reminder reminder : reminders) {
				bookingService.remindStudentsAboutNextClass(reminder);
				List<Instant> updatedRemindAt = removeMatchingRemindAtDates(before, reminder);
				reminder.setRemindAt(updatedRemindAt);
				// remove if no more reminder date
				// update if there remains dates
				if(updatedRemindAt.isEmpty()) {
					reminderRepository.delete(reminder);
				} else {
					reminder.setRemindAt(updatedRemindAt);
					reminderRepository.save(reminder);
				}
			}
		} catch(RemindBookingException e) {
			log.warn("Failed to remind students about next class", e);
			// TODO: Should handle properly this error
		}
	}

	private List<Instant> removeMatchingRemindAtDates(Instant before, Reminder reminder) {
		List<Instant> updatedRemindAt = reminder.getRemindAt()
			.stream()
			.dropWhile(i -> i.isBefore(before))
			.collect(toList());
		return updatedRemindAt;
	}
}
