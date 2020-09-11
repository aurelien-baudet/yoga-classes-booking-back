package fr.yoga.booking.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.yoga.booking.controller.dto.ErrorDto;
import fr.yoga.booking.service.business.exception.AlreadyRegisteredUser;
import fr.yoga.booking.service.business.exception.reservation.AlreadyBookedException;
import fr.yoga.booking.service.business.exception.reservation.NotBookedException;
import fr.yoga.booking.service.business.exception.user.ExpiredResetTokenException;
import fr.yoga.booking.service.business.exception.user.InvalidResetTokenException;
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
	
	@ExceptionHandler(AlreadyBookedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDto alreadyBookedException(AlreadyBookedException e) {
		return new ErrorDto("ALREADY_BOOKED", e)
				.addData("bookedClassId", e.getBookedClass().getId())
				.addData("studentId", e.getStudent().getId());
	}
	
	@ExceptionHandler(NotBookedException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDto notBookedException(NotBookedException e) {
		return new ErrorDto("NOT_BOOKED", e)
				.addData("classId", e.getBookedClass().getId())
				.addData("studentId", e.getStudent().getId());
	}
	
	@ExceptionHandler(InvalidResetTokenException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDto invalid(InvalidResetTokenException e) {
		return new ErrorDto("INVALID_TOKEN", e);
	}
	
	@ExceptionHandler(ExpiredResetTokenException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorDto expired(ExpiredResetTokenException e) {
		return new ErrorDto("EXPIRED_TOKEN", e);
	}
}
