package fr.yoga.booking.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.ResetPassword;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.PasswordResetException;

@RestController
@RequestMapping("users/password")
public class PasswordController {
	@Autowired
	UserService userService;
	
	@GetMapping
	@ResponseStatus(ACCEPTED)
	public void requestPasswordReset(@RequestParam("contact") String emailOrPhoneNumber) throws PasswordResetException {
		userService.requestPasswordReset(emailOrPhoneNumber);
	}
	
	@PostMapping(params = "code")
	public void validateResetCode(@RequestParam("code") String code) throws PasswordResetException {
		userService.validateResetToken(code);
	}
	
	@PostMapping
	public void confirmPasswordReset(@RequestBody ResetPassword resetPassword) throws PasswordResetException {
		userService.resetPassword(resetPassword.getToken(), resetPassword.getNewPassword());
	}
}
