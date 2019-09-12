package fr.yoga.booking.service.business;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.reservation.AlreadyBookedException;
import fr.yoga.booking.service.business.exception.reservation.BookingException;
import fr.yoga.booking.service.business.exception.reservation.NotBookedException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
	private final ScheduledClassRepository scheduledClassRepository;
	private final NotificationService notificationService;
	private final WaitingListStrategy waitingListStrategy;
	
	public ScheduledClass book(ScheduledClass bookedClass, Student student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentInfo(student), bookedBy);
	}
	
	public ScheduledClass book(ScheduledClass bookedClass, UnregisteredUser student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentInfo(student), bookedBy);
	}

	public ScheduledClass unbook(ScheduledClass bookedClass, Student student, User canceledBy) throws BookingException {
		return unbook(bookedClass, new StudentInfo(student), canceledBy);
	}

	public ScheduledClass unbook(ScheduledClass bookedClass, UnregisteredUser student, User canceledBy) throws BookingException {
		return unbook(bookedClass, new StudentInfo(student), canceledBy);
	}
	
	public List<ScheduledClass> listBookedClassesBy(Student student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	public List<ScheduledClass> listBookedClassesBy(UnregisteredUser student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	public List<Booking> listApprovedBookings(ScheduledClass scheduledClass) {
		return scheduledClass.sortedByAscendingDateBookings()
				.stream()
				.filter(c -> c.isApproved())
				.collect(toList());
	}

	public List<Booking> listWaitingBookings(ScheduledClass scheduledClass) {
		return scheduledClass.sortedByAscendingDateBookings()
				.stream()
				.filter(c -> !c.isApproved())
				.collect(toList());
	}

	
	private ScheduledClass book(ScheduledClass bookedClass, StudentInfo student, User bookedBy) throws BookingException {
		if(alreadyBooked(bookedClass, student)) {
			throw new AlreadyBookedException(bookedClass, student);
		}
		ScheduledClass updatedClass = bookedClass.addBooking(new Booking(Instant.now(), bookedBy, student, isApproved(bookedClass)));
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.booked(updatedClass, student, bookedBy);
		return updatedClass;
	}

	private ScheduledClass unbook(ScheduledClass bookedClass, StudentInfo student, User canceledBy) throws NotBookedException {
		if(notBooked(bookedClass, student)) {
			throw new NotBookedException(bookedClass, student);
		}
		ScheduledClass updatedClass = bookedClass.removeBookingForStudent(student);
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.unbooked(updatedClass, student, canceledBy);
		// handle waiting list
		return waitingListStrategy.placeFreed(updatedClass);
	}

	private boolean isApproved(ScheduledClass scheduledClass) {
		int maxStudents = scheduledClass.getLesson().getInfo().getMaxStudents();
		int numBookings = scheduledClass.getBookings().size();
		return numBookings < maxStudents;
	}

	private boolean alreadyBooked(ScheduledClass bookedClass, StudentInfo student) {
		return scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}

	private boolean notBooked(ScheduledClass bookedClass, StudentInfo student) {
		return !scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}
}
