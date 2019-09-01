package fr.yoga.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.ScheduledClassDto;
import fr.yoga.booking.controller.mapper.ScheduledClassMapper;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.service.business.BookingService;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.reservation.BookingException;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.technical.security.UserDetailsWrapper;

@RestController
@RequestMapping("classes")
public class BookingController {
	@Autowired
	BookingService bookingService;
	@Autowired
	UserService userService;
	@Autowired
	ClassService classService;
	@Autowired
	ScheduledClassMapper classMapper;
	
	@PostMapping("{classId}/bookings/{studentId}")
	public ScheduledClassDto book(@PathVariable("classId") String classId, @PathVariable("studentId") String studentId, @AuthenticationPrincipal UserDetailsWrapper currentUser) throws UserException, ScheduledClassException, BookingException {
		User bookedBy = userService.getUser(currentUser != null ? currentUser.getUser().getId() : studentId);
		Student student = userService.getStudent(studentId);
		ScheduledClass bookedClass = classService.getClass(classId);
		return classMapper.toDto(bookingService.book(bookedClass, student, bookedBy));
	}
	
	@PostMapping("{classId}/bookings")
	public ScheduledClassDto book(@PathVariable("classId") String classId, @RequestBody UnregisteredUser student) throws UserException, ScheduledClassException, BookingException {
		// TODO: handle case where a connected user books for another unregistered user
//		User bookedBy = userService.getUser(currentUser != null ? currentUser.getUser().getId() : studentId);
		ScheduledClass bookedClass = classService.getClass(classId);
		return classMapper.toDto(bookingService.book(bookedClass, student, null));
	}
	
	@DeleteMapping("{classId}/bookings/{studentId}")
	public ScheduledClassDto cancel(@PathVariable("classId") String classId, @PathVariable("studentId") String studentId, @AuthenticationPrincipal UserDetailsWrapper currentUser) throws UserException, ScheduledClassException, BookingException {
		User canceledBy = userService.getUser(currentUser != null ? currentUser.getUser().getId() : studentId);
		Student student = userService.getStudent(studentId);
		ScheduledClass bookedClass = classService.getClass(classId);
		return classMapper.toDto(bookingService.cancel(bookedClass, student, canceledBy));
	}
	
	@DeleteMapping("{classId}/bookings")
	public ScheduledClassDto cancel(@PathVariable("classId") String classId, @RequestBody UnregisteredUser student) throws UserException, ScheduledClassException, BookingException {
		// TODO: handle case where a connected user cancels for another unregistered user
//		User canceledBy = userService.getUser(currentUser != null ? currentUser.getUser().getId() : studentId);
		ScheduledClass bookedClass = classService.getClass(classId);
		return classMapper.toDto(bookingService.cancel(bookedClass, student, null));
	}
	
	@GetMapping("bookings/{studentId}")
	public List<ScheduledClassDto> listBookedClasses(@PathVariable("studentId") String studentId) throws UserException {
		Student student = userService.getStudent(studentId);
		return classMapper.toDto(bookingService.listBookedClassesBy(student));
	}
	
	@GetMapping("bookings")
	public List<ScheduledClassDto> listBookedClasses(@RequestParam("displayName") String displayName, 
													@RequestParam(name="email", required=false) String email,
													@RequestParam(name="phoneNumber", required=false) String phoneNumber) throws UserException {
		UnregisteredUser student = new UnregisteredUser(displayName, email, phoneNumber);
		return classMapper.toDto(bookingService.listBookedClassesBy(student));
	}
}
