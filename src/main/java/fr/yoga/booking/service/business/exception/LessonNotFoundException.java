package fr.yoga.booking.service.business.exception;

import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;
import lombok.Getter;

@Getter
public class LessonNotFoundException extends ScheduledClassException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String lessonId;

	public LessonNotFoundException(String lessonId, String message, Throwable cause) {
		super(message, cause);
		this.lessonId = lessonId;
	}

	public LessonNotFoundException(String lessonId, String message) {
		super(message);
		this.lessonId = lessonId;
	}

	public LessonNotFoundException(String lessonId, Throwable cause) {
		super(cause);
		this.lessonId = lessonId;
	}

	public LessonNotFoundException(String lessonId) {
		super("The lesson or event doesn't exist");
		this.lessonId = lessonId;
	}

}
