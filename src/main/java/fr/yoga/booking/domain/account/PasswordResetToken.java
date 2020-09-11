package fr.yoga.booking.domain.account;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {
	@Id
	private String id;
	private User user;
	private String token;
	private Instant creationDate;
	
	public PasswordResetToken(User user, String token) {
		this(null, user, token, Instant.now());
	}
}
