package fr.yoga.booking.service.technical.security;

import static java.time.Instant.now;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.PasswordResetToken;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.repository.PasswordResetTokenRepository;
import fr.yoga.booking.service.business.PasswordResetProperties;
import fr.yoga.booking.service.business.exception.user.ExpiredResetTokenException;
import fr.yoga.booking.service.business.exception.user.InvalidResetTokenException;
import fr.yoga.booking.service.business.exception.user.PasswordResetException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TokenService {
	private final PasswordResetTokenRepository tokenRepository;
	private final RandomGenerator random;
	private final PasswordResetProperties resetProperties;

	public String generateResetToken(Student student, String emailOrPhoneNumber) {
		String token = random.generate(resetProperties.getTokenLength());
		tokenRepository.save(new PasswordResetToken(student, token));
		return token;
	}

	public User validateResetToken(String token) throws PasswordResetException {
		PasswordResetToken match = tokenRepository.findOneByToken(token);
		if (match == null) {
			throw new InvalidResetTokenException(token);
		}
		if (expired(match)) {
			throw new ExpiredResetTokenException(token);
		}
		return match.getUser();
	}

	public void invalidateResetToken(String token) {
		tokenRepository.deleteByToken(token);
	}
	
	private boolean expired(PasswordResetToken match) {
		return match.getCreationDate().plus(resetProperties.getTokenValidity()).isBefore(now());
	}
}
