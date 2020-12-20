package fr.yoga.booking.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.yoga.booking.controller.dto.ErrorDto;
import fr.yoga.booking.service.business.exception.AlreadyRegisteredUser;
import fr.yoga.booking.service.business.exception.reservation.AlreadyBookedException;
import fr.yoga.booking.service.business.exception.reservation.CantRemoveClassWithBookersException;
import fr.yoga.booking.service.business.exception.reservation.NotBookedException;
import fr.yoga.booking.service.business.exception.reservation.PlaceAlreadyTakenBySomeoneElseException;
import fr.yoga.booking.service.business.exception.reservation.PlaceAlreadyTakenException;
import fr.yoga.booking.service.business.exception.reservation.TooLateToUnbookException;
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
	
	@ExceptionHandler(PlaceAlreadyTakenException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDto placeAlreadyTaken(PlaceAlreadyTakenException e) {
		return new ErrorDto("PLACE_ALREADY_TAKEN", e)
				.addData("classId", e.getBookedClass().getId())
				.addData("studentId", e.getStudent().getId());
	}
	
	@ExceptionHandler(PlaceAlreadyTakenBySomeoneElseException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDto placeAlreadyTaken(PlaceAlreadyTakenBySomeoneElseException e) {
		return new ErrorDto("PLACE_ALREADY_TAKEN_BY_SOMEONE_ELSE", e)
				.addData("classId", e.getBookedClass().getId())
				.addData("studentId", e.getStudent().getId());
	}
	
	@ExceptionHandler(TooLateToUnbookException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorDto expired(TooLateToUnbookException e) {
		return new ErrorDto("TOO_LATE_TO_UNBOOK", e)
				.addData("classId", e.getBookedClass().getId())
				.addData("studentId", e.getStudent().getId());
	}
	
	@ExceptionHandler(CantRemoveClassWithBookersException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorDto expired(CantRemoveClassWithBookersException e) {
		return new ErrorDto("CANT_REMOVE_CLASS_WITH_BOOKERS", e)
				.addData("classId", e.getScheduledClass().getId());
	}
}
