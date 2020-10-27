package fr.yoga.booking.domain.reservation;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.reservation.Booking.SortByAscendingDateComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Document
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ScheduledClass {
	@Id
	private String id;
	private Instant start;
	private Instant end;
	private Lesson lesson;
	private ClassState state;
	private List<Booking> bookings;
	
	public ScheduledClass() {
		this(null, null, null, null, new Opened(), new ArrayList<>());
	}
	
	public ScheduledClass(Instant start, Instant end, Lesson lesson) {
		this(null, start, end, lesson, new Opened(), new ArrayList<>());
	}

	
	public ScheduledClass addBooking(Booking booking) {
		getBookings().add(booking);
		return this;
	}
	
	public ScheduledClass removeBooking(Booking booking) {
		List<Booking> filtered = getBookings()
			.stream()
			.filter(b -> !b.isSame(booking))
			.collect(toList());
		setBookings(filtered);
		return this;
	}
	
	public ScheduledClass removeBookingForStudent(StudentRef bookedFor) {
		List<Booking> filtered = getBookings()
			.stream()
			.filter(booking -> !booking.isForStudent(bookedFor))
			.collect(toList());
		setBookings(filtered);
		return this;
	}

	public List<Booking> sortedByAscendingDateBookings() {
		List<Booking> bookings = new ArrayList<>(getBookings());
		bookings.sort(new SortByAscendingDateComparator());
		return bookings;
	}

	public ScheduledClass updateBooking(Booking booking) {
		removeBooking(booking);
		addBooking(booking);
		return this;
	}
	
	public List<StudentRef> allStudents() {
		return getBookings()
				.stream()
				.map(b -> b.getStudent())
				.collect(toList());
	}
	
	public boolean isApprovedFor(StudentRef student) {
		return getBookings()
				.stream()
				.filter(b -> b.isForStudent(student))
				.anyMatch(b -> b.isApproved());
	}
	
	public boolean isWaitingFor(StudentRef student) {
		return getBookings()
				.stream()
				.filter(b -> b.isForStudent(student))
				.anyMatch(b -> !b.isApproved());
	}
	
	public List<StudentRef> approvedStudents() {
		return sortedByAscendingDateBookings()
				.stream()
				.filter(c -> c.isApproved())
				.map(Booking::getStudent)
				.collect(toList());
	}
	
	public List<StudentRef> waitingStudents() {
		return sortedByAscendingDateBookings()
				.stream()
				.filter(c -> !c.isApproved())
				.map(Booking::getStudent)
				.collect(toList());
	}
	
	@Transient
	public boolean isApprovedListFull() {
		int maxStudents = getLesson().getInfo().getMaxStudents();
		int numBookings = approvedStudents().size();
		return numBookings >= maxStudents;
	}
	

	@Transient
	public boolean isCanceled() {
		return getState() instanceof Canceled;
	}
	
	public boolean isSame(ScheduledClass other) {
		if (id == null) {
			return false;
		}
		return id.equals(other.getId());
	}
}
