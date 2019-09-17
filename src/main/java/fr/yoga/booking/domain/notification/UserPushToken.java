package fr.yoga.booking.domain.notification;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.account.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class UserPushToken {
	@Id
	private String id;
	private User user;
	private String token;
	private Instant registrationDate;
	
	public UserPushToken(User user, String token) {
		this(null, user, token, Instant.now());
	}
}
