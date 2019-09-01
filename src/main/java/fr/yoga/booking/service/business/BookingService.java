package fr.yoga.booking.service.business;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.ScheduledClass;
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
	
	public ScheduledClass book(ScheduledClass bookedClass, Student student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentInfo(student), bookedBy);
	}
	
	public ScheduledClass book(ScheduledClass bookedClass, UnregisteredUser student, User bookedBy) throws BookingException {
		return book(bookedClass, new StudentInfo(student), bookedBy);
	}

	public ScheduledClass cancel(ScheduledClass bookedClass, Student student, User canceledBy) throws BookingException {
		return cancel(bookedClass, new StudentInfo(student), canceledBy);
	}

	public ScheduledClass cancel(ScheduledClass bookedClass, UnregisteredUser student, User canceledBy) throws BookingException {
		return cancel(bookedClass, new StudentInfo(student), canceledBy);
	}
	
	public List<ScheduledClass> listBookedClassesBy(Student student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	public List<ScheduledClass> listBookedClassesBy(UnregisteredUser student) {
		return scheduledClassRepository.findNextBookedClassesForStudent(student);
	}
	
	public List<Booking> listApprovedBookings(ScheduledClass scheduledClass) {
		int maxStudents = scheduledClass.getLesson().getInfo().getMaxStudents();
		int numBookings = scheduledClass.getBookings().size();
		return getSortedBookings(scheduledClass).subList(0, Math.min(maxStudents, numBookings));
	}

	public List<Booking> listWaitingBookings(ScheduledClass scheduledClass) {
		int maxStudents = scheduledClass.getLesson().getInfo().getMaxStudents();
		int numBookings = scheduledClass.getBookings().size();
		if(numBookings <= maxStudents) {
			return emptyList();
		}
		return getSortedBookings(scheduledClass).subList(maxStudents, numBookings);
	}

	
	private ScheduledClass book(ScheduledClass bookedClass, StudentInfo student, User bookedBy) throws BookingException {
		if(alreadyBooked(bookedClass, student)) {
			throw new AlreadyBookedException(bookedClass, student);
		}
		ScheduledClass updatedClass = addBooking(bookedClass, new Booking(Instant.now(), bookedBy, student));
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.booked(updatedClass, student, bookedBy);
		return updatedClass;
	}

	private ScheduledClass cancel(ScheduledClass bookedClass, StudentInfo student, User canceledBy) throws NotBookedException {
		if(notBooked(bookedClass, student)) {
			throw new NotBookedException(bookedClass, student);
		}
		ScheduledClass updatedClass = removeBookingForStudent(bookedClass, student);
		updatedClass = scheduledClassRepository.save(updatedClass);
		// notify student
		notificationService.bookingCanceled(updatedClass, student, canceledBy);
		return updatedClass;
	}

	private boolean alreadyBooked(ScheduledClass bookedClass, StudentInfo student) {
		return scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}

	private boolean notBooked(ScheduledClass bookedClass, StudentInfo student) {
		return !scheduledClassRepository.existsBookedClassForStudent(bookedClass, student);
	}

	private List<Booking> getSortedBookings(ScheduledClass scheduledClass) {
		List<Booking> bookings = new ArrayList<>(scheduledClass.getBookings());
		bookings.sort(new BookingComparator());
		return bookings;
	}
	
	private ScheduledClass addBooking(ScheduledClass bookedClass, Booking booking) {
		bookedClass.getBookings().add(booking);
		return bookedClass;
	}
	
	private ScheduledClass removeBookingForStudent(ScheduledClass bookedClass, StudentInfo bookedFor) {
		List<Booking> filtered = bookedClass.getBookings()
			.stream()
			.filter(booking -> !booking.isForStudent(bookedFor))
			.collect(toList());
		bookedClass.setBookings(filtered);
		return bookedClass;
	}
	
	private static class BookingComparator implements Comparator<Booking> {
		@Override
		public int compare(Booking o1, Booking o2) {
			return o1.getBookDate().compareTo(o2.getBookDate());
		}
	}
}
