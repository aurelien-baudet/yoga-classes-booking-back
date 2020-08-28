package fr.yoga.booking.service.business;

import java.time.Instant;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.reservation.BookingException;
import fr.yoga.booking.service.business.exception.reservation.PlaceAlreadyTakenBySomeoneElseException;
import fr.yoga.booking.service.business.exception.reservation.PlaceAlreadyTakenException;
import fr.yoga.booking.service.business.security.annotation.CanTakeAvailablePlace;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FirstToConfirmTakesAvailablePlaceWaitingUserStrategy implements WaitingListStrategy, ConfirmBookingStrategy {
	private final ScheduledClassRepository scheduledClassRepository;
	private final NotificationService notificationService;

	@Override
	public ScheduledClass placeFreed(ScheduledClass scheduledClass) {
		// notify students that a class is available
		// TODO: only notify student once ?
		notificationService.availablePlace(scheduledClass, scheduledClass.waitingStudents());
		return scheduledClass;
	}

	@Override
	@CanTakeAvailablePlace
	public ScheduledClass confirm(ScheduledClass bookedClass, StudentRef student, User bookedBy) throws BookingException {
		if (placeAlreadyTaken(bookedClass, student)) {
			throw new PlaceAlreadyTakenException(bookedClass, student);
		}
		if (placeAlreadyTakenBySomeoneElse(bookedClass)) {
			throw new PlaceAlreadyTakenBySomeoneElseException(bookedClass, student);
		}
		return takeAvailablePlace(bookedClass, student, bookedBy);
	}

	private ScheduledClass takeAvailablePlace(ScheduledClass bookedClass, StudentRef student, User bookedBy) {
		ScheduledClass updatedClass = bookedClass.removeBookingForStudent(student)
				.addBooking(new Booking(Instant.now(), bookedBy, student, true));
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.booked(updatedClass, student, bookedBy);
		return updatedClass;
	}

	private boolean placeAlreadyTaken(ScheduledClass bookedClass, StudentRef student) {
		return bookedClass.isApprovedFor(student);
	}

	private boolean placeAlreadyTakenBySomeoneElse(ScheduledClass bookedClass) {
		return bookedClass.isApprovedListFull();
	}


}
