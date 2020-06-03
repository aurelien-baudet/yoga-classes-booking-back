package fr.yoga.booking.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnregisteredUserPreferences {
	private Boolean sendBookedMail;
	private Boolean agreesToBeAssisted;
}
