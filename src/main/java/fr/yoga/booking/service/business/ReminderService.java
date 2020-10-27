package fr.yoga.booking.service.business;

import static fr.yoga.booking.util.DateUtil.midday;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Order.asc;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.subscription.PeriodCard;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.ReminderProperties.SubscriptionProperties;
import fr.yoga.booking.service.business.exception.reservation.RemindBookingException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.technical.scheduling.SchedulingHelper;
import fr.yoga.booking.service.technical.scheduling.Trigger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
	private final SchedulingHelper helper;
	private final ScheduledClassRepository classesRepository;
	private final BookingService bookingService;
	private final SubscriptionService subscriptionService;
	private final UserService userService;
	private final ReminderProperties reminderProperties;

	
	@Scheduled(initialDelay = 0, fixedRateString="${reminder.register-interval}")
	public void registerRemindersForNextClasses() {
		for (ScheduledClass scheduledClass : findFutureClasses()) {
			List<Trigger<ScheduledClass>> reminders = toReminders(scheduledClass);
			helper.scheduleOnceFutureOrMostRecentTriggers(reminders);
		}
	}

	private List<ScheduledClass> findFutureClasses() {
		return classesRepository.findByStartAfter(now(), Sort.by(asc("start")));
	}

	private List<Trigger<ScheduledClass>> toReminders(ScheduledClass scheduledClass) {
		List<Trigger<ScheduledClass>> reminders = new ArrayList<>();
		for (Duration remindBefore : reminderProperties.getNextClass().getTriggerBefore()) {
			reminders.add(new Trigger<>("next-class|"+scheduledClass.getId()+"|"+remindBefore, 
					scheduledClass, 
					"next-class",
					scheduledClass.getStart().minus(remindBefore), 
					getRemindAboutNextClassTask(remindBefore, scheduledClass),
					scheduledClass.getStart()));
		}
		return reminders;
	}
	
	private Runnable getRemindAboutNextClassTask(Duration remindBefore, ScheduledClass nextClass) {
		return () -> {
			try {
				log.info("Trigger reminder {} before start of class {} ({})", remindBefore, nextClass.getId(), nextClass.getStart());
				bookingService.remindStudentsAboutNextClass(nextClass);
			} catch (RemindBookingException e) {
				log.warn("Failed to remind students about next class", e);
				throw new RuntimeException(e);
			}
		};
	}

	@Scheduled(initialDelay = 0, fixedRateString="${reminder.register-interval}")
	public void registerRemindersForSubscriptions() {
		for (UserSubscriptions subscription : findSubscriptionsThatAreExpiredOrAboutToExpire()) {
			try {
				List<Trigger<UserSubscriptions>> reminders = toReminders(subscription);
				helper.scheduleOnceFutureOrMostRecentTriggers(reminders);
			} catch (UserException e) {
				log.warn("Fail to remind user about subscription expiration", e);
			}
		}
	}


	private List<UserSubscriptions> findSubscriptionsThatAreExpiredOrAboutToExpire() {
		return subscriptionService.getCurrentSubscriptions().stream()
				.filter(subscriptionService::isExpiredOrAboutToExpire)
				.collect(toList());
	}

	private List<Trigger<UserSubscriptions>> toReminders(UserSubscriptions subscription) throws UserException {
		List<Trigger<UserSubscriptions>> reminders = new ArrayList<>();
		SubscriptionProperties subscriptionProps = reminderProperties.getSubscription();
		if (subscriptionService.isAnnualCardAboutToExpire(subscription)) {
			addRemindersToTriggerBeforeExpirationDate(subscription, subscription.getAnnualCard(), subscriptionProps.getAnnualCard().getTriggerBeforeExpiration(), reminders);
			addRemindersToTriggerBeforeNextClass(subscription, subscriptionProps.getAnnualCard().getTriggerBeforeNextClass(), reminders);
			return reminders;
		}
		if (subscriptionService.isMonthCardAboutToExpire(subscription)) {
			addRemindersToTriggerBeforeExpirationDate(subscription, subscription.getMonthCard(), subscriptionProps.getMonthCard().getTriggerBeforeExpiration(), reminders);
			addRemindersToTriggerBeforeNextClass(subscription, subscriptionProps.getMonthCard().getTriggerBeforeNextClass(), reminders);
			return reminders;
		}
		if (subscriptionService.isNotEnoughRemainingClasses(subscription)) {
			addRemindersToTriggerBeforeNextClass(subscription, subscriptionProps.getRemainingClasses().getTriggerBeforeNextClass(), reminders);
			return reminders;
		}
		// TODO: merge to avoid double notification if same trigger date ?
		return reminders;
	}

	private void addRemindersToTriggerBeforeNextClass(UserSubscriptions subscription, SortedSet<Duration> remindBeforeSet, List<Trigger<UserSubscriptions>> reminders) throws UserException {
		ScheduledClass nextClassForStudent = bookingService.getNextClassForStudent(userService.getRegisteredStudent(subscription.getSubscriber()));
		if (nextClassForStudent == null) {
			return;
		}
		for (Duration remindBefore : remindBeforeSet) {
			reminders.add(new Trigger<>("subscription-next-class|"+subscription.getId()+"|"+nextClassForStudent.getId()+"|"+remindBefore, 
					subscription, 
					"subscription-next-class",
					nextClassForStudent.getStart().minus(remindBefore), 
					getRemindToRenewSubscriptionBeforeNextClass(remindBefore, subscription, nextClassForStudent),
					nextClassForStudent.getStart()));
		}
	}

	private Runnable getRemindToRenewSubscriptionBeforeNextClass(Duration remindBefore, UserSubscriptions subscription, ScheduledClass nextClass) {
		return () -> {
			log.info("Trigger reminder {} before start of next class {} ({})", remindBefore, nextClass.getId(), nextClass.getStart());
			subscriptionService.remindToRenewSubscription(subscription, nextClass);
		};
	}

	private void addRemindersToTriggerBeforeExpirationDate(UserSubscriptions subscription, PeriodCard card, SortedSet<Duration> remindBeforeSet, List<Trigger<UserSubscriptions>> reminders) {
		for (Duration remindBefore : remindBeforeSet) {
			reminders.add(new Trigger<>("subscription-expiration|"+subscription.getId()+"|"+remindBefore, 
					subscription, 
					"subscription-expiration",
					midday(card.getEnd().minus(remindBefore)), 
					getRemindToRenewSubscriptionBeforeExpiration(remindBefore, subscription, card),
					card.getEnd()));
		}
	}

	
	private Runnable getRemindToRenewSubscriptionBeforeExpiration(Duration remindBefore, UserSubscriptions subscription, PeriodCard card) {
		return () -> {
			log.info("Trigger reminder {} before expiration of card ({})", remindBefore, card.getEnd());
			subscriptionService.remindToRenewSubscription(subscription);
		};
	}



}
