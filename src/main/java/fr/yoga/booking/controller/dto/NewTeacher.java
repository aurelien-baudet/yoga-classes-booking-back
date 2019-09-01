package fr.yoga.booking.controller.dto;

import fr.yoga.booking.domain.account.Credentials;
import lombok.Data;

@Data
public class NewTeacher {
	private String displayName;
	private Credentials credentials;
}
