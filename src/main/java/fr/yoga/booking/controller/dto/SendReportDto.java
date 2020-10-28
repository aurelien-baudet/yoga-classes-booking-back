package fr.yoga.booking.controller.dto;

import fr.yoga.booking.domain.notification.Notification;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Data;

@Data
public class SendReportDto {
	private StudentRef student;
	private Notification notification;
	private boolean success;
	private MessageStatusDto pushNotification;
	private MessageStatusDto email;
	private MessageStatusDto sms;
}
