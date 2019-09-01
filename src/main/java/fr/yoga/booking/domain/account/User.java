package fr.yoga.booking.domain.account;

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
	
	public User(String displayName, Account account) {
		this(null, displayName, account);
	}

	public boolean isSame(User other) {
		return other != null && id.equals(other.getId());
	}
}
