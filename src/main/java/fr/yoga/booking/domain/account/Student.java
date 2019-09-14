package fr.yoga.booking.domain.account;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Document
@EqualsAndHashCode(callSuper=true)
public class Student extends User {
	@NotNull
	private ContactInfo contact;
	@NotNull
	private Preferences preferences;
	
	public Student() {
		this(null);
	}
	
	public Student(String id) {
		super(id, null, null);
		contact = new ContactInfo();
		preferences = new Preferences();
	}
	
	public Student(String displayName, Account account, ContactInfo contact, Preferences preferences) {
		super(displayName, account);
		this.contact = contact;
		this.preferences = preferences;
	}
}
