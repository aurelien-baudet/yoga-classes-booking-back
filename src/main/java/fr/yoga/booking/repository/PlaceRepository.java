package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.reservation.Place;

public interface PlaceRepository extends MongoRepository<Place, String> {

}
