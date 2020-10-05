package fr.yoga.booking.domain.account;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Teacher extends User {
	
	public Teacher(String displayName, Account account) {
		super(displayName, account);
	}
	
	public Teacher(String displayName, Account account, ContactInfo contact) {
		super(displayName, account, contact);
	}
}
