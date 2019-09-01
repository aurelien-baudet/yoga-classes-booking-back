package fr.yoga.booking.service.business.exception.reservation;

import fr.yoga.booking.controller.dto.NewLesson;
import lombok.Getter;

@Getter
public class ClassRegistrationException extends ScheduledClassException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final NewLesson newLessonData;
	
	public ClassRegistrationException(NewLesson data, String message, Throwable cause) {
		super(message, cause);
		this.newLessonData = data;
	}

}
