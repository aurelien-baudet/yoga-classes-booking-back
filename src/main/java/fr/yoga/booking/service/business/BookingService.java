package fr.yoga.booking.service.business;

import static fr.yoga.booking.domain.account.Role.GOD;
import static fr.yoga.booking.domain.account.Role.TEACHER;
import static fr.yoga.booking.util.UserUtils.hasAnyRole;
import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.notification.Reminder;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.reservation.AlreadyBookedException;
import fr.yoga.booking.service.business.exception.reservation.BookingException;
import fr.yoga.booking.service.business.exception.reservation.NotBookedException;
import fr.yoga.booking.service.business.exception.reservation.RemindBookingException;
import fr.yoga.booking.service.business.exception.reservation.TooLateToUnbookException;
import fr.yoga.booking.service.business.security.annotation.CanBookClass;
import fr.yoga.booking.service.business.security.annotation.CanListApprovedBookings;
import fr.yoga.booking.service.business.security.annotation.CanListBookedClasses;
import fr.yoga.booking.service.business.security.annotation.CanListWaitingBookings;
import fr.yoga.booking.service.business.security.annotation.CanUnbookClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
	private final ScheduledClassRepository scheduledClassRepository;
	private final NotificationService notificationService;
	private final WaitingListStrategy waitingListStrategy;
	private final ConfirmBookingStrategy confirmStrategy;
	private final BookingProperties bookingProperties;
	
	@CanBookClass
	public ScheduledClass book(ScheduledClass bookedClass, Student student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentRef(student), bookedBy);
	}
	
	@CanBookClass
	public ScheduledClass book(ScheduledClass bookedClass, UnregisteredUser student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentRef(student), bookedBy);
	}
	
	@CanBookClass
	public ScheduledClass book(ScheduledClass bookedClass, StudentRef student, User bookedBy) throws BookingException {
		if(alreadyBooked(bookedClass, student)) {
			throw new AlreadyBookedException(bookedClass, student);
		}
		ScheduledClass updatedClass = bookedClass.addBooking(new Booking(Instant.now(), bookedBy, student, isAutomaticallyApproved(bookedClass)));
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.booked(updatedClass, student, bookedBy);
		return updatedClass;
	}


	@CanUnbookClass
	public ScheduledClass unbook(ScheduledClass bookedClass, Student student, User canceledBy) throws BookingException {
		return unbook(bookedClass, new StudentRef(student), canceledBy);
	}

	@CanUnbookClass
	public ScheduledClass unbook(ScheduledClass bookedClass, UnregisteredUser student, User canceledBy) throws BookingException {
		return unbook(bookedClass, new StudentRef(student), canceledBy);
	}
	
	@CanUnbookClass
	public ScheduledClass unbook(ScheduledClass bookedClass, StudentRef student, User canceledBy) throws BookingException {
		if(notBooked(bookedClass, student)) {
			throw new NotBookedException(bookedClass, student);
		}
		checkCanUnbook(bookedClass, student, canceledBy);
		boolean wasApproved = bookedClass.isApprovedFor(student);
		ScheduledClass updatedClass = bookedClass.removeBookingForStudent(student);
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.unbooked(updatedClass, student, canceledBy);
		// if student was in approved list => handle the free spot
		// otherwise, nothing more to do
		if (wasApproved) {
			// handle waiting list
			return waitingListStrategy.placeFreed(updatedClass);
		}
		return updatedClass;
	}
	

	@CanListBookedClasses
	public List<ScheduledClass> listBookedClassesBy(StudentRef student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	@CanListBookedClasses
	public List<ScheduledClass> listBookedClassesBy(Student student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	@CanListBookedClasses
	public List<ScheduledClass> listBookedClassesBy(UnregisteredUser student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	@CanListApprovedBookings
	public List<Booking> listApprovedBookings(ScheduledClass scheduledClass) {
		return scheduledClass.sortedByAscendingDateBookings()
				.stream()
				.filter(c -> c.isApproved())
				.collect(toList());
	}

	@CanListWaitingBookings
	public List<Booking> listWaitingBookings(ScheduledClass scheduledClass) {
		return scheduledClass.sortedByAscendingDateBookings()
				.stream()
				.filter(c -> !c.isApproved())
				.collect(toList());
	}

	public ScheduledClass confirm(ScheduledClass bookedClass, StudentRef student, User bookedBy) throws BookingException {
		return confirmStrategy.confirm(bookedClass, student, bookedBy);
	}
	
	public void remindStudentsAboutNextClass(Reminder reminder) throws RemindBookingException {
		ScheduledClass nextClass = scheduledClassRepository.findById(reminder.getScheduledClass().getId()).orElse(null);
		if (nextClass == null) {
			return;
		}
		List<StudentRef> approvedStudents = listApprovedBookings(nextClass)
				.stream()
				.map(b -> b.getStudent())
				.collect(toList());
		notificationService.reminder(nextClass, approvedStudents);
	}


	private boolean isAutomaticallyApproved(ScheduledClass scheduledClass) {
		return !scheduledClass.isApprovedListFull();
	}

	private boolean alreadyBooked(ScheduledClass bookedClass, StudentRef student) {
		return scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}

	private boolean notBooked(ScheduledClass bookedClass, StudentRef student) {
		return !scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}
	
	private void checkCanUnbook(ScheduledClass bookedClass, StudentRef student, User canceledBy) throws BookingException {
		if (hasAnyRole(canceledBy, GOD, TEACHER)) {
			return;
		}
		if (now().isAfter(bookedClass.getStart().minus(bookingProperties.getUnbookUntil()))) {
			throw new TooLateToUnbookException(bookedClass, student);
		}
	}
}
