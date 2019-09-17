package fr.yoga.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.NewPlace;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.service.business.PlaceService;
import fr.yoga.booking.service.business.exception.PlaceException;

@RestController
@RequestMapping("places")
public class PlaceController {
	@Autowired
	PlaceService placeService;

	@GetMapping
	public List<Place> list() {
		return placeService.getPlaces();
	}

	@GetMapping("{id}")
	public Place getPlaceInfo(@PathVariable("id") String placeId) throws PlaceException {
		return placeService.getPlace(placeId);
	}

	@PostMapping
	public Place add(@RequestBody NewPlace place) throws PlaceException {
		return placeService.register(place.getName(), place.getAddress(), place.getMaps());
	}

}
