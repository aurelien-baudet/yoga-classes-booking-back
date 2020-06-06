package fr.yoga.booking.domain.account;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class Account {
	private String login;
	@JsonIgnore()
	private String password;
	private Set<Role> roles;
	
	public Account(Credentials credentials, Role role) {
		this(credentials.getLogin(), credentials.getPassword(), new HashSet<>(asList(role)));
	}
	
	public Account(String login, String password, Role role) {
		this(login, password, new HashSet<>(asList(role)));
	}
	
	@JsonIgnore(false)
	public void setPassword(String password) {
		this.password = password;
	}
}
