package fr.yoga.booking.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnregisteredUser {
	private String displayName;
	private String email;
	private String phoneNumber;
	private Boolean sendBookedMail;
	
	public UnregisteredUser(String displayName, String email, String phoneNumber) {
		this(displayName, email, phoneNumber, null);
	}
	
	public boolean isSame(UnregisteredUser other) {
		return displayName.equals(other.getDisplayName())
				&& isSameEmail(other.getEmail())
				&& isSamePhoneNumber(other.getPhoneNumber()); 
	}

	private boolean isSameEmail(String email) {
		if(this.email == null) {
			return email == null;
		}
		return this.email.equals(email);
	}

	private boolean isSamePhoneNumber(String phoneNumber) {
		if(this.phoneNumber == null) {
			return phoneNumber == null;
		}
		return this.phoneNumber.equals(phoneNumber);
	}
}
