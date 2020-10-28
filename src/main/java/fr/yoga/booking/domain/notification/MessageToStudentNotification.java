package fr.yoga.booking.domain.notification;

import static fr.yoga.booking.domain.notification.NotificationType.MESSAGE_TO_STUDENT;

import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Data;

@Data
public class MessageToStudentNotification implements Notification {
	private final Teacher sender;
	private final StudentRef student;
	private final String message;

	@Override
	public NotificationType getType() {
		return MESSAGE_TO_STUDENT;
	}

}
