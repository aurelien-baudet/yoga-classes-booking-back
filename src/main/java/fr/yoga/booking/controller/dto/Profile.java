package fr.yoga.booking.controller.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fr.yoga.booking.domain.account.ContactInfo;
import lombok.Data;

@Data
@Valid
public class Profile {
	@NotNull
	@NotEmpty
	private String displayName;
	private ContactInfo contact;
}
