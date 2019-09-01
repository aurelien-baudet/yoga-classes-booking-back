package fr.yoga.booking.controller.dto;

import java.util.ArrayList;
import java.util.List;

import fr.yoga.booking.domain.reservation.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingsDto {
	private List<Booking> all = new ArrayList<>();
	private List<Booking> approved = new ArrayList<>();
	private List<Booking> waiting = new ArrayList<>();
}
