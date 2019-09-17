package fr.yoga.booking.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.yoga.booking.controller.dto.ErrorDto;
import fr.yoga.booking.service.business.exception.AlreadyRegisteredUser;
import fr.yoga.booking.service.business.exception.user.StudentNotFoundException;

@RestControllerAdvice
public class BusinessErrorExceptionTranslator {
	@ExceptionHandler(StudentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDto studentNotFound(StudentNotFoundException e) {
		return new ErrorDto("STUDENT_NOT_FOUND", e)
				.addData("studentId", e.getUserId());
	}
	
	@ExceptionHandler(AlreadyRegisteredUser.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDto alreadyRegisteredUser(AlreadyRegisteredUser e) {
		return new ErrorDto("LOGIN_ALREADY_USED", e)
				.addData("login", e.getUser().getAccount().getLogin());
	}
}
