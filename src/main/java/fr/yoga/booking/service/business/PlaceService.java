package fr.yoga.booking.service.business;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.Image;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.repository.PlaceRepository;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.PlaceNotFoundException;
import fr.yoga.booking.service.business.security.annotation.CanListPlaces;
import fr.yoga.booking.service.business.security.annotation.CanRegisterPlace;
import fr.yoga.booking.service.business.security.annotation.CanViewPlace;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	private final PlaceRepository placeRepository;

	@CanViewPlace
	public Place getPlace(String placeId) throws PlaceException {
		return placeRepository.findById(placeId)
				.orElseThrow(() -> new PlaceNotFoundException(placeId));
	}

	@CanRegisterPlace
	public Place register(String name, String address, List<Image> maps) throws PlaceException {
		return placeRepository.save(new Place(name, address, maps));
	}

	@CanListPlaces
	public List<Place> getPlaces() {
		return placeRepository.findAll();
	}
}
