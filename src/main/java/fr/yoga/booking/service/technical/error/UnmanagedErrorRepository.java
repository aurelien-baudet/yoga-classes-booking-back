package fr.yoga.booking.service.technical.error;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UnmanagedErrorRepository extends MongoRepository<UnmanagedError, String> {

}
