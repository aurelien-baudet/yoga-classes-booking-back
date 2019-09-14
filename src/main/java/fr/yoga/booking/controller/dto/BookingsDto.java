package fr.yoga.booking.controller.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingsDto {
	private List<BookingDto> all = new ArrayList<>();
	private List<BookingDto> approved = new ArrayList<>();
	private List<BookingDto> waiting = new ArrayList<>();
}
