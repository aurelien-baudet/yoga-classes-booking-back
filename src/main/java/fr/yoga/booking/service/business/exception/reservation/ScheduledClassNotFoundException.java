package fr.yoga.booking.service.business.exception.reservation;

import lombok.Getter;

@Getter
public class ScheduledClassNotFoundException extends ScheduledClassException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String classId;

	public ScheduledClassNotFoundException(String classId, String message, Throwable cause) {
		super(message, cause);
		this.classId = classId;
	}

	public ScheduledClassNotFoundException(String classId, String message) {
		super(message);
		this.classId = classId;
	}

	public ScheduledClassNotFoundException(String classId, Throwable cause) {
		super(cause);
		this.classId = classId;
	}

	public ScheduledClassNotFoundException(String classId) {
		super("The reservation doesn't exist");
		this.classId = classId;
	}

}
