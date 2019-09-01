package fr.yoga.booking.controller.dto;

import java.time.Instant;

import fr.yoga.booking.domain.reservation.ClassState;
import lombok.Data;

@Data
public class ScheduledClassDto {
	private String id;
	private Instant start;
	private Instant end;
	private LessonDto lesson;
	private ClassState state;
	private BookingsDto bookings;
}
