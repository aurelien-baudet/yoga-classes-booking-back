package fr.yoga.booking.service.business.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Credentials;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityService {
	private final PasswordEncoder passwordEncoder;
	
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}
	
	public Credentials encodePassword(Credentials credentials) {
		return new Credentials(credentials.getLogin(), encodePassword(credentials.getPassword()));
	}

}
