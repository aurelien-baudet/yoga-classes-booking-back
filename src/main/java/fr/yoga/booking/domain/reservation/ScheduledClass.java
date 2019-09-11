package fr.yoga.booking.domain.reservation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
