package fr.yoga.booking.controller.dto;

import lombok.Data;

@Data
public class MessageStatusDto {
	private boolean sent;
	private ErrorDto failure;
}
