package fr.yoga.booking.domain.account;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class User {
	@Id
	private String id;
	private String displayName;
	private Account account;
	@NotNull
	private ContactInfo contact;
	
	public User(String id) {
		this(id, null, null, new ContactInfo());
	}

	public User(String displayName, Account account) {
		this(displayName, account, new ContactInfo());
	}

	public User(String displayName, Account account, ContactInfo contact) {
		this(null, displayName, account, contact);
	}

	public boolean isSame(User other) {
		return other != null && isSame(other.getId());
	}
	
	public boolean isSame(String otherId) {
		return otherId != null && id.equals(otherId);
	}
}
