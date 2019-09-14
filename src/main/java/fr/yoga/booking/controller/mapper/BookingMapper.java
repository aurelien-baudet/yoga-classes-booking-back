package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.yoga.booking.controller.dto.BookingDto;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.UserException;

@Mapper
public abstract class BookingMapper {
	@Autowired
	UserService userService;

	@Mapping(target = "student", expression = "java(userService.anonymize(booking.getStudent()))")
	public abstract BookingDto toDto(Booking booking) throws UserException;

	public abstract List<BookingDto> toDto(List<Booking> bookings);
}
