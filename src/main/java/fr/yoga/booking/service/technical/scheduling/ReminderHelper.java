package fr.yoga.booking.service.technical.scheduling;

import static java.time.Instant.now;
import static java.util.Collections.synchronizedList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.notification.Reminded;
import fr.yoga.booking.domain.notification.Reminder;
import fr.yoga.booking.repository.RemindedRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderHelper {
	private final RemindedRepository remindedRepository;
	private final TaskScheduler scheduler;
	private final List<Reminder<?>> registered = synchronizedList(new ArrayList<>());

	public <T> void scheduleOnceFutureOrMostRecentReminders(List<Reminder<T>> reminders) {
		schedule(keepNotAlreadyTriggeredReminders(keepUnregisteredReminders(keepFutureOrMostRecentInPastReminders(reminders))));
	}
	
	public <T> void schedule(List<Reminder<T>> reminders) {
		registered.addAll(reminders);
		for (Reminder<T> reminder : reminders) {
			log.debug("Register reminder {} at {}", reminder.getId(), reminder.getTriggerAt());
			scheduler.schedule(new ReminderTask(reminder, this::markAsTriggered), reminder.getTriggerAt());
		}
	}

	public <T> List<Reminder<T>> keepUnregisteredReminders(List<Reminder<T>> reminders) {
		return reminders.stream()
				.filter(not(registered::contains))
				.collect(toList());
	}

	public <T> List<Reminder<T>> keepFutureOrMostRecentInPastReminders(List<Reminder<T>> reminders) {
		return reminders.stream()
				.filter(r -> futureOrMostRecentInPast(r, reminders))
				.collect(toList());
	}

	public <T> List<Reminder<T>> keepNotAlreadyTriggeredReminders(List<Reminder<T>> reminders) {
		return reminders.stream()
				.filter(not(this::alreadyReminded))
				.collect(toList());
	}
	
	public void clean() {
		registered.clear();
	}

	@Scheduled(initialDelay = 0, fixedRateString="${reminder.clean-interval}")
	public void autoclean() {
		synchronized (registered) {
			registered.removeIf(this::canClean);
		}
	}
	
	private <T> boolean futureOrMostRecentInPast(Reminder<T> reminder, List<Reminder<T>> associatedReminders) {
		if (isInTheFuture(reminder)) {
			return true;
		}
		// reminders are sorted according to original duration (from properties)
		// => benefit from it to use findFirst()
		Reminder<T> mostRecentInPast = associatedReminders.stream()
				.filter(this::isInThePast)
				.findFirst()
				.orElse(null);
		if (mostRecentInPast == null) {
			// should never happen
			return true;
		}
		if (!reminder.equals(mostRecentInPast)) {
			log.info("Reminder {} skipped because there is another past reminder that is more recent (would have been triggered at {})", reminder.getId(), reminder.getTriggerAt());
		}
		return reminder.equals(mostRecentInPast);
	}

	private <T> boolean isInThePast(Reminder<T> r) {
		return r.getTriggerAt().isBefore(now());
	}

	private <T> boolean isInTheFuture(Reminder<T> reminder) {
		return reminder.getTriggerAt().isAfter(now());
	}
	
	private <T> void markAsTriggered(Reminder<T> reminder) {
		remindedRepository.save(new Reminded(reminder));
	}

	private <T> boolean alreadyReminded(Reminder<T> reminder) {
		return remindedRepository.existsByReminderId(reminder.getId());
	}

	private boolean canClean(Reminder<?> reminder) {
		boolean cleanable = now().isAfter(reminder.getCleanableAt());
		if (cleanable) {
			log.debug("Clean reminder {}", reminder.getId());
		}
		return cleanable;
	}

	@RequiredArgsConstructor
	@Getter
	public static class ReminderTask implements Runnable {
		private final Reminder<?> reminder;
		private final Consumer<Reminder<?>> markAsTriggered;

		@Override
		public void run() {
			try {
				reminder.getTask().run();
				markAsTriggered.accept(reminder);
			} catch(RuntimeException e) {
				log.warn("Failed to trigger reminder {}", reminder.getId(), e.getCause());
			}
		}
	}
}
