package fr.yoga.booking.domain.account;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnregisteredUser {
	private String id;
	private String displayName;
	@NotNull
	private ContactInfo contact;
	@NotNull
	private UnregisteredUserPreferences preferences;

	public UnregisteredUser(String id) {
		this(id, null, new ContactInfo(), new UnregisteredUserPreferences());
	}
	
	public UnregisteredUser(String id, String displayName, ContactInfo contact, String phoneNumber) {
		this(id, displayName, contact, new UnregisteredUserPreferences());
	}

	public UnregisteredUser(String displayName, ContactInfo contact, UnregisteredUserPreferences preferences) {
		this(null, displayName, contact, preferences);
	}
	
//	public boolean isSame(UnregisteredUser other) {
//		return uid.equals(anObject)
////		return displayName.equals(other.getDisplayName())
////				&& isSameContact(other.getContact()); 
//	}

	public boolean isSame(UnregisteredUser other) {
		return other != null && isSame(other.getId());
	}
	
	public boolean isSame(String otherId) {
		return otherId != null && id.equals(otherId);
	}
	
//	private boolean isSameContact(ContactInfo contact) {
//		return isSameEmail(contact.getEmail())
//				&& isSamePhoneNumber(contact.getPhoneNumber()); 		
//	}
//	
//	private boolean isSameEmail(String email) {
//		if(contact.getEmail() == null) {
//			return email == null;
//		}
//		return contact.getEmail().equals(email);
//	}
//
//	private boolean isSamePhoneNumber(String phoneNumber) {
//		if(contact.getPhoneNumber() == null) {
//			return phoneNumber == null;
//		}
//		return contact.getPhoneNumber().equals(phoneNumber);
//	}
}
