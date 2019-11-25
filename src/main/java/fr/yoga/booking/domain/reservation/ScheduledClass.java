package fr.yoga.booking.domain.reservation;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.reservation.Booking.SortByAscendingDateComparator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Document
@AllArgsConstructor
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
	
	public ScheduledClass removeBookingForStudent(StudentInfo bookedFor) {
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
	
	public List<StudentInfo> allStudents() {
		return getBookings()
				.stream()
				.map(b -> b.getStudent())
				.collect(toList());
	}
	
	public boolean isApprovedFor(StudentInfo student) {
		return getBookings()
				.stream()
				.filter(b -> b.isForStudent(student))
				.anyMatch(b -> b.isApproved());
	}
}
