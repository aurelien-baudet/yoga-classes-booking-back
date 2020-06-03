package fr.yoga.booking.controller.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.UnregisteredUserPreferences;
import lombok.Data;

@Data
public class NewUnregisteredUser {
	@NotNull
	@NotEmpty
	private String displayName;
	private ContactInfo contact;
	private UnregisteredUserPreferences preferences;
}
