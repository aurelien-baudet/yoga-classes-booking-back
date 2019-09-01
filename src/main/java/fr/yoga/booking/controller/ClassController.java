package fr.yoga.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.ScheduledClassDto;
import fr.yoga.booking.controller.mapper.ScheduledClassMapper;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.PlaceService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;

@RestController
@RequestMapping("classes")
public class ClassController {
	@Autowired
	ClassService classService;
	@Autowired
	PlaceService placeService;
	@Autowired
	UserService userService;
	@Autowired
	ScheduledClassMapper classMapper;

	@PatchMapping("{classId}/places/{newPlaceId}")
	public ScheduledClassDto changePlace(@PathVariable("classId") String classId, @PathVariable("newPlaceId") String newPlaceId) throws ScheduledClassException, PlaceException {
		return classMapper.toDto(classService.changePlace(classService.getClass(classId), placeService.getPlace(newPlaceId)));
	}
	
	@PatchMapping("{classId}")
	public ScheduledClassDto cancel(@PathVariable String classId) throws ScheduledClassException {
		return classMapper.toDto(classService.cancel(classService.getClass(classId)));
	}
	
	@GetMapping("{classId}")
	public ScheduledClassDto getClass(@PathVariable String classId) throws ScheduledClassException {
		return classMapper.toDto(classService.getClass(classId));
	}

	@GetMapping
	public List<ScheduledClassDto> listFutureClasses() {
		return classMapper.toDto(classService.listFutureClasses());
	}
}
