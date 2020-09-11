package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.account.PasswordResetToken;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
	PasswordResetToken findOneByToken(String token);
	void deleteByToken(String token);
}
