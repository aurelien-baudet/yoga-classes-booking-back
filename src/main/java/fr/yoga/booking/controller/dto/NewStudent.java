package fr.yoga.booking.controller.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Credentials;
import fr.yoga.booking.domain.account.Preferences;
import lombok.Data;

@Data
public class NewStudent {
	@NotNull
	@NotEmpty
	private String displayName;
	@Valid
	private Credentials credentials;
	private ContactInfo contact;
	private Preferences preferences;
}
