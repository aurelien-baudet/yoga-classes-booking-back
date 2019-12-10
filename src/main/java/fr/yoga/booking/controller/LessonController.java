package fr.yoga.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.DateRange;
import fr.yoga.booking.controller.dto.LessonDto;
import fr.yoga.booking.controller.dto.NewLesson;
import fr.yoga.booking.controller.dto.ScheduledClassDto;
import fr.yoga.booking.controller.mapper.LessonMapper;
import fr.yoga.booking.controller.mapper.ScheduledClassMapper;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.LessonInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.PlaceService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.user.UserException;

@RestController
@RequestMapping("lessons")
public class LessonController {
	@Autowired
	ClassService classService;
	@Autowired
	PlaceService placeService;
	@Autowired
	UserService userService;
	@Autowired
	ScheduledClassMapper classMapper;
	@Autowired
	LessonMapper lessonMapper;

	@PostMapping
	public LessonDto register(@RequestBody NewLesson data) throws ScheduledClassException, PlaceException, UserException {
		Place place = placeService.getPlace(data.getPlaceId());
		Teacher teacher = userService.getTeacher(data.getTeacherId());
		return lessonMapper.toDto(classService.register(data.getInfo(), place, teacher));
	}
	
	@PatchMapping("{lessonId}")
	public ScheduledClassDto schedule(@PathVariable("lessonId") String lessonId, @RequestBody DateRange schedule) throws ScheduledClassException {
		return classMapper.toDto(classService.schedule(classService.getLesson(lessonId), schedule.getStart(), schedule.getEnd()));
	}
	
	@GetMapping("{lessonId}")
	public LessonDto getLessonInfo(@PathVariable String lessonId) throws ScheduledClassException {
		return lessonMapper.toDto(classService.getLesson(lessonId));
	}

	@GetMapping
	public List<LessonDto> list() {
		return lessonMapper.toDto(classService.listLessons());
	}
	
	@GetMapping(params="unscheduled")
	public List<LessonDto> listUnscheduled() {
		return lessonMapper.toDto(classService.listUnscheduledLessons());
	}

	@PatchMapping("{lessonId}/places/{newPlaceId}")
	public LessonDto updatePlace(@PathVariable("lessonId") String lessonId, @PathVariable("newPlaceId") String newPlaceId) throws ScheduledClassException, PlaceException {
		Place newPlace = placeService.getPlace(newPlaceId);
		return lessonMapper.toDto(classService.updatePlaceForAllClasses(classService.getLesson(lessonId), newPlace));
	}

	@PatchMapping("{lessonId}/info")
	public LessonDto updateLessonInfo(@PathVariable("lessonId") String lessonId, @RequestBody LessonInfo newInfo) throws ScheduledClassException {
		return lessonMapper.toDto(classService.updateLessonForAllClasses(classService.getLesson(lessonId), newInfo));
	}
}
