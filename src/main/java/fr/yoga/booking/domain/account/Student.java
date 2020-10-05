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
	private Preferences preferences;
	
	public Student() {
		this(null);
	}
	
	public Student(String id) {
		super(id);
		preferences = new Preferences();
	}
	
	public Student(String displayName, Account account, ContactInfo contact, Preferences preferences) {
		super(displayName, account, contact);
		this.preferences = preferences;
	}
}
