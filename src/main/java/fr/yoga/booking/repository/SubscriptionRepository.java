package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.subscription.UserSubscriptions;

public interface SubscriptionRepository extends MongoRepository<UserSubscriptions, String> {

	UserSubscriptions findOneBySubscriberId(String subscriberId);

}
