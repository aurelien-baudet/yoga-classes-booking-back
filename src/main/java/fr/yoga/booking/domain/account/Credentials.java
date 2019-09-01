package fr.yoga.booking.domain.account;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
	@NotNull
	@NotEmpty
	private String login;
	@NotNull
	@NotEmpty
	private String password;
}
