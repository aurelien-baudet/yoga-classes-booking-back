package fr.yoga.booking.service.business;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.ScheduledClassRepository;
import lombok.Data;

@Data
@Service
public class AutomaticallyAcceptFirstWaitingUserStrategy implements WaitingListStrategy {
	private final ScheduledClassRepository scheduledClassRepository;
	private final NotificationService notificationService;

	@Override
	public ScheduledClass placeFreed(ScheduledClass scheduledClass) {
		Booking firstWaiting = getFirstWaitingBooking(scheduledClass);
		if (firstWaiting == null) {
			return scheduledClass;
		}
		firstWaiting.setApproved(true);
		ScheduledClass updatedClass = scheduledClass.updateBooking(firstWaiting);
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student that he has been automatically registered
		// after a place has been freed
		notificationService.freePlaceBooked(updatedClass, firstWaiting);
		return updatedClass;
	}

	private Booking getFirstWaitingBooking(ScheduledClass scheduledClass) {
		Booking firstWaiting = scheduledClass.sortedByAscendingDateBookings()
			.stream()
			.filter(c -> !c.isApproved())
			.findFirst()
			.orElse(null);
		return firstWaiting;
	}

}
