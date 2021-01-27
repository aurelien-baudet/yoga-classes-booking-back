package fr.yoga.booking.controller.dto;

import java.time.Instant;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Data;

@Data
public class BookingDto {
	private Instant bookDate;
	private UserRef bookedBy;
	private StudentRef student;
}
