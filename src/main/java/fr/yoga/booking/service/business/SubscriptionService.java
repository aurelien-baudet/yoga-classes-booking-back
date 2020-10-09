package fr.yoga.booking.service.business;

import static fr.yoga.booking.util.DateUtil.endOfDay;
import static fr.yoga.booking.util.DateUtil.startOfDay;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.subscription.PeriodCard;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.SubscriptionRepository;
import fr.yoga.booking.service.business.security.annotation.CanListSubscriptions;
import fr.yoga.booking.service.business.security.annotation.CanUpdateSubscriptionsForStudent;
import fr.yoga.booking.service.business.security.annotation.CanViewSubscriptionsForStudent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
	private final SubscriptionRepository subscriptionRepository;
	private final LowBalanceProperties lowBalanceProperties;
	private final NotificationService notificationService;
	private final StudentRepository studentRepository;

	@CanUpdateSubscriptionsForStudent
	public UserSubscriptions addPaidClasses(Student student, int numPaid) {
		UserSubscriptions current = getSubscriptionsFor(student);
		return updateSubscriptions(current, numPaid + current.getRemainingClasses(), null, null);
	}

	@CanUpdateSubscriptionsForStudent
	public UserSubscriptions addMonthCard(Student student, Instant start) {
		UserSubscriptions current = getSubscriptionsFor(student);
		return updateSubscriptions(current, null, new PeriodCard(start, start.plus(30, DAYS)), null);
	}

	@CanUpdateSubscriptionsForStudent
	public UserSubscriptions addAnnualCard(Student student, Instant start) {
		UserSubscriptions current = getSubscriptionsFor(student);
		return updateSubscriptions(current, null, null, new PeriodCard(start, start.plus(365, DAYS)));
	}

	@CanUpdateSubscriptionsForStudent
	public UserSubscriptions updateSubscriptions(Student student, int remainingClasses, PeriodCard month, PeriodCard annual) {
		UserSubscriptions current = getSubscriptionsFor(student);
		return updateSubscriptions(current, remainingClasses, month, annual);
	}

	@CanViewSubscriptionsForStudent
	public UserSubscriptions getCurrentSubscriptionsFor(Student student) {
		return getSubscriptionsFor(student);
	}
	
	@CanListSubscriptions
	public Page<UserSubscriptions> getSubscriptionsForAllStudents(Pageable page) {
		Page<Student> students = studentRepository.findAll(page);
		List<UserSubscriptions> subscriptionsPerStudent = students.getContent().stream()
				.map(this::getSubscriptionsFor)
				.collect(toList());
		return new PageImpl<>(subscriptionsPerStudent, students.getPageable(), students.getTotalElements());
	}

	public UserSubscriptions takePartInClass(Student student, ScheduledClass followedClass) {
		UserSubscriptions current = getSubscriptionsFor(student);
		if (current.hasValidAnnualCard() || current.hasValidMonthCard()) {
			return current;
		}
		current.decreasePaid(1);
		return subscriptionRepository.save(current);
	}
	

	public UserSubscriptions getSubscriptionsFor(Student student) {
		UserSubscriptions subscription = subscriptionRepository.findOneBySubscriberId(student.getId());
		if (subscription == null) {
			return subscriptionRepository.save(new UserSubscriptions(student));
		}
		return subscription;
	}
	
	public List<UserSubscriptions> getCurrentSubscriptions() {
		return subscriptionRepository.findAll();
	}

	public boolean isExpiredOrAboutToExpire(UserSubscriptions subscription) {
		if (isAnnualCardAboutToExpire(subscription)) {
			return true;
		}
		if (isMonthCardAboutToExpire(subscription)) {
			return true;
		}
		if (isNotEnoughRemainingClasses(subscription)) {
			return true;
		}
		return false;
	}
	
	public boolean isNotEnoughRemainingClasses(UserSubscriptions subscription) {
		if (subscription.hasValidAnnualCard() || subscription.hasValidMonthCard()) {
			return false;
		}
		return subscription.getRemainingClasses() <= lowBalanceProperties.getRemainingClasses();
	}

	public boolean isAnnualCardAboutToExpire(UserSubscriptions subscription) {
		return isAboutToExpireDueToPeriodCard(subscription.hasValidAnnualCard(), subscription.getAnnualCard(), lowBalanceProperties.getAnnualCardRemainingDuration());
	}

	public boolean isMonthCardAboutToExpire(UserSubscriptions subscription) {
		return isAboutToExpireDueToPeriodCard(subscription.hasValidMonthCard(), subscription.getMonthCard(), lowBalanceProperties.getMonthCardRemainingDuration());
	}
	
	public void remindToRenewSubscription(UserSubscriptions subscription, ScheduledClass nextClass) {
		if (nextClass.isCanceled()) {
			return;
		}
		remindToRenewSubscription(subscription);
	}

	public void remindToRenewSubscription(UserSubscriptions subscription) {
		UserSubscriptions upToDate = subscriptionRepository.findById(subscription.getId()).orElse(null);
		if (upToDate == null) {
			return;
		}
		if (remedied(upToDate)) {
			return;
		}
		if (isAnnualCardAboutToExpire(upToDate)) {
			notificationService.renewAnnualCard(upToDate);
			return;
		}
		if (isMonthCardAboutToExpire(upToDate)) {
			notificationService.renewMonthCard(upToDate);
			return;
		}
		if (isNotEnoughRemainingClasses(upToDate) && upToDate.getRemainingClasses() < 0) {
			notificationService.renewClassPackageCard(upToDate);
			return;
		}
		notificationService.unpaidClasses(upToDate);
	}

	private UserSubscriptions updateSubscriptions(UserSubscriptions current, Integer remainingClasses, PeriodCard month, PeriodCard annual) {
		if (remainingClasses != null) {
			current.setRemainingClasses(remainingClasses);
		}
		if (month != null) {
			current.setMonthCard(fullDays(month));
		}
		if (annual != null) {
			current.setAnnualCard(fullDays(annual));
		}
		return subscriptionRepository.save(current);
	}

	private PeriodCard fullDays(PeriodCard card) {
		return new PeriodCard(startOfDay(card.getStart()), endOfDay(card.getEnd()));
	}

	private boolean isAboutToExpireDueToPeriodCard(boolean hasValidCard, PeriodCard card, Duration lowBalance) {
		return hasValidCard && now().isAfter(card.getEnd().minus(lowBalance));
	}

	private boolean remedied(UserSubscriptions subscription) {
		return !isExpiredOrAboutToExpire(subscription);
	}
}
