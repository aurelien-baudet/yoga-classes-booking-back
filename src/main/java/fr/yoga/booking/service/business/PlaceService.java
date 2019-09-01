package fr.yoga.booking.service.business;

import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.repository.PlaceRepository;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.PlaceNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	private final PlaceRepository placeRepository;

	public Place getPlace(String placeId) throws PlaceException {
		return placeRepository.findById(placeId)
				.orElseThrow(() -> new PlaceNotFoundException(placeId));
	}

	public Place register(String name, String address, URL plan) throws PlaceException {
		return placeRepository.save(new Place(name, address, plan));
	}

	public List<Place> getPlaces() {
		return placeRepository.findAll();
	}
}
