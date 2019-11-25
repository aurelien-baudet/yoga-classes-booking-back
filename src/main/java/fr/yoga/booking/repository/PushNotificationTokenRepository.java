package fr.yoga.booking.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.notification.UserPushToken;

public interface PushNotificationTokenRepository extends MongoRepository<UserPushToken, String> {
	boolean existsByUserIdAndToken(String userId, String token);
	boolean existsByUserId(String userId);

	UserPushToken findFirstByUserIdOrderByRegistrationDateDesc(String userId);
	List<UserPushToken> findByUserIdOrderByRegistrationDateDesc(String userId);

	void deleteByUserId(String userId);
}
