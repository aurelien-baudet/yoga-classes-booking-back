package fr.yoga.booking.domain.account;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Student extends User {
	private ContactInfo contact;
	private Preferences preferences;
	
	public Student(String id) {
		super(id, null, null);
	}
	
	public Student(String displayName, Account account, ContactInfo contact, Preferences preferences) {
		super(displayName, account);
		this.contact = contact;
		this.preferences = preferences;
	}
}
