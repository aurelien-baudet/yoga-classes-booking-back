package fr.yoga.booking.service.technical.scheduling;

import static java.time.Instant.now;
import static java.util.Collections.synchronizedList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Order.desc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.data.domain.Sort;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulingHelper {
	private final TriggeredRepository triggeredRepository;
	private final TaskScheduler scheduler;
	private final List<Trigger<?>> registered = synchronizedList(new ArrayList<>());

	public <T> void scheduleOnceFutureOrMostRecentTriggers(List<Trigger<T>> triggers) {
		schedule(keepNotAlreadyTriggered(keepUnregistered(keepFutureOrMostRecentInPast(triggers))));
	}

	public <T> void schedule(List<Trigger<T>> triggers) {
		for (Trigger<T> trigger : triggers) {
			schedule(trigger);
		}
	}

	public <T> void scheduleOnce(Trigger<T> trigger) {
		schedule(keepNotAlreadyTriggered(keepUnregistered(trigger)));
	}

	public <T> void schedule(Trigger<T> trigger) {
		if (trigger == null) {
			return;
		}
		registered.add(trigger);
		log.debug("Register trigger {} at {}", trigger.getId(), trigger.getTriggerAt());
		scheduler.schedule(new ReminderTask(trigger, this::markAsTriggered), trigger.getTriggerAt());
	}
	
	public void clean() {
		registered.clear();
	}

	@Scheduled(initialDelay = 0, fixedRateString="${scheduler.clean-interval}")
	public void autoclean() {
		synchronized (registered) {
			registered.removeIf(this::canClean);
		}
	}
	
	public Triggered getLatestTriggeredForType(String type) {
		return triggeredRepository.findFirstByTriggerType(type, Sort.by(desc("triggeredAt")));
	}
	

	private <T> List<Trigger<T>> keepUnregistered(List<Trigger<T>> triggers) {
		return triggers.stream()
				.filter(not(this::alreadyRegistered))
				.collect(toList());
	}

	private <T> Trigger<T> keepUnregistered(Trigger<T> trigger) {
		if (trigger == null) {
			return null;
		}
		if (alreadyRegistered(trigger)) {
			return null;
		}
		return trigger;
	}
	
	private <T> boolean alreadyRegistered(Trigger<T> trigger) {
		return registered.contains(trigger);
	}

	private <T> List<Trigger<T>> keepFutureOrMostRecentInPast(List<Trigger<T>> triggers) {
		return triggers.stream()
				.filter(r -> futureOrMostRecentInPast(r, triggers))
				.collect(toList());
	}

	private <T> List<Trigger<T>> keepNotAlreadyTriggered(List<Trigger<T>> triggers) {
		return triggers.stream()
				.filter(not(this::alreadyTriggered))
				.collect(toList());
	}
	
	private <T> Trigger<T> keepNotAlreadyTriggered(Trigger<T> trigger) {
		if (trigger == null) {
			return null;
		}
		if (alreadyTriggered(trigger)) {
			return null;
		}
		return trigger;
	}

	private <T> boolean alreadyTriggered(Trigger<T> trigger) {
		return triggeredRepository.existsByTriggerId(trigger.getId());
	}
	
	private <T> boolean futureOrMostRecentInPast(Trigger<T> trigger, List<Trigger<T>> associatedTriggers) {
		if (isInTheFuture(trigger)) {
			return true;
		}
		// reminders are sorted according to original duration (from properties)
		// => benefit from it to use findFirst()
		Trigger<T> mostRecentInPast = associatedTriggers.stream()
				.filter(this::isInThePast)
				.findFirst()
				.orElse(null);
		if (mostRecentInPast == null) {
			// should never happen
			return true;
		}
		if (!trigger.equals(mostRecentInPast)) {
			log.info("Trigger {} skipped because there is another past trigger that is more recent (would have been triggered at {})", trigger.getId(), trigger.getTriggerAt());
		}
		return trigger.equals(mostRecentInPast);
	}

	private <T> boolean isInThePast(Trigger<T> trigger) {
		return trigger.getTriggerAt().isBefore(now());
	}

	private <T> boolean isInTheFuture(Trigger<T> trigger) {
		return trigger.getTriggerAt().isAfter(now());
	}
	
	private <T> void markAsTriggered(Trigger<T> trigger) {
		triggeredRepository.save(new Triggered(trigger));
	}

	private boolean canClean(Trigger<?> trigger) {
		if (trigger.getCleanableAt() == null) {
			return false;
		}
		boolean cleanable = now().isAfter(trigger.getCleanableAt());
		if (cleanable) {
			log.debug("Clean reminder {}", trigger.getId());
		}
		return cleanable;
	}

	@RequiredArgsConstructor
	@Getter
	public static class ReminderTask implements Runnable {
		private final Trigger<?> trigger;
		private final Consumer<Trigger<?>> markAsTriggered;

		@Override
		public void run() {
			try {
				trigger.getTask().run();
				markAsTriggered.accept(trigger);
			} catch(RuntimeException e) {
				log.warn("Failed to run trigger task {}", trigger.getId(), e.getCause());
			}
		}
	}
}
