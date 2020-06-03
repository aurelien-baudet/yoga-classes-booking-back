package fr.yoga.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.domain.account.Preferences;
import fr.yoga.booking.domain.account.UnregisteredUserPreferences;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.UserException;

@RestController
@RequestMapping("users")
public class PreferencesController {
	@Autowired
	UserService userService;

	@GetMapping("{userId}/preferences")
	public Preferences getRegisteredUserPreferences(@PathVariable String userId) throws UserException {
		return userService.getRegisteredStudent(userId).getPreferences();
	}

	@GetMapping("/unregistered/{unregisteredId}/preferences")
	public UnregisteredUserPreferences getUnregisteredUserPreferences(@PathVariable String unregisteredId) throws UserException {
		return userService.getUnregisteredStudent(unregisteredId).getPreferences();
	}

}
