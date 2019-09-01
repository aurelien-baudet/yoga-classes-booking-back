package fr.yoga.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.NewStudent;
import fr.yoga.booking.controller.dto.NewTeacher;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.technical.security.UserDetailsWrapper;

@RestController
@RequestMapping("users")
public class UserController {
	@Autowired
	UserService userService;
	
	@GetMapping
	public User getCurrentUserInfo(@AuthenticationPrincipal UserDetailsWrapper wrapper) throws UserException {
		if(wrapper == null) {
			return null;
		}
		return userService.getUser(wrapper.getUser().getId());
	}

	@GetMapping("{userId}")
	public User getUserInfo(@PathVariable String userId) throws UserException {
		return userService.getUser(userId);
	}

	@PostMapping("students")
	public Student regiterStudent(@RequestBody NewStudent newStudent) throws AccountException {
		return userService.registerStudent(newStudent.getDisplayName(), newStudent.getCredentials(), newStudent.getContact(), newStudent.getPreferences());
	}

	@PostMapping("teachers")
	public Teacher regiterTeacher(@RequestBody NewTeacher newTeacher) throws AccountException {
		return userService.registerTeacher(newTeacher.getDisplayName(), newTeacher.getCredentials());
	}
}
