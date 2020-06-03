package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.account.UnregisteredUser;

public interface UnregisteredUserRepository extends MongoRepository<UnregisteredUser, String> {
}
