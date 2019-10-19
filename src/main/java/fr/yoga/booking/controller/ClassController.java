package fr.yoga.booking.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.ScheduledClassDto;
import fr.yoga.booking.controller.mapper.ScheduledClassMapper;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.LessonInfo;
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
	public ScheduledClassDto cancel(@PathVariable String classId, @RequestBody CancelData data) throws ScheduledClassException {
		return classMapper.toDto(classService.cancel(classService.getClass(classId), data));
	}
	
	@GetMapping("{classId}")
	public ScheduledClassDto getClass(@PathVariable String classId) throws ScheduledClassException {
		return classMapper.toDto(classService.getClass(classId));
	}

	@GetMapping
	public List<ScheduledClassDto> listFutureClasses() {
		return classMapper.toDto(classService.listFutureClasses());
	}

	@GetMapping(params={"lesson"})
	public List<ScheduledClassDto> listClassesFor(@RequestParam("lesson") String lessonId, 
												  @RequestParam(name="from", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate from, 
												  @RequestParam(name="to", required=false)   @DateTimeFormat(iso = ISO.DATE) LocalDate to) throws ScheduledClassException {
		return classMapper.toDto(classService.listClassesFor(classService.getLesson(lessonId), toInstant(from), toInstant(to)));
	}

	@PatchMapping("{classId}/lesson/info")
	public ScheduledClassDto updateLessonInfo(@PathVariable("classId") String classId, @RequestBody LessonInfo newInfo) throws ScheduledClassException, PlaceException {
		return classMapper.toDto(classService.updateLessonInfoForSpecificClass(classService.getClass(classId), newInfo));
	}

	private Instant toInstant(LocalDate date) {
		if (date == null) {
			return null;
		}
		return date.atStartOfDay().toInstant(ZoneOffset.UTC);
	}
}
