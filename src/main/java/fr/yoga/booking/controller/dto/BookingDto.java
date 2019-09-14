package fr.yoga.booking.controller.dto;

import java.time.Instant;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentInfo;
import lombok.Data;

@Data
public class BookingDto {
	private Instant bookDate;
	private User bookedBy;
	private StudentInfo student;
}
