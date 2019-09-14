package fr.yoga.booking.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import fr.yoga.booking.controller.dto.ScheduledClassDto;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.service.business.BookingService;

@Mapper(uses= {LessonMapper.class, BookingMapper.class})
public abstract class ScheduledClassMapper {
	@Autowired
	BookingService bookingService;

	@Mapping(target = "bookings.all", source = "bookings")
	@Mapping(target = "bookings.approved", expression = "java(bookingMapper.toDto(bookingService.listApprovedBookings(scheduledClass)))")
	@Mapping(target = "bookings.waiting", expression = "java(bookingMapper.toDto(bookingService.listWaitingBookings(scheduledClass)))")
	public abstract ScheduledClassDto toDto(ScheduledClass scheduledClass);

	public abstract List<ScheduledClassDto> toDto(List<ScheduledClass> scheduledClass);
}
