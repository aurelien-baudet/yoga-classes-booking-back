package fr.yoga.booking.service.business;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.Image;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.repository.LessonRepository;
import fr.yoga.booking.repository.PlaceRepository;
import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.exception.PlaceException;
import fr.yoga.booking.service.business.exception.PlaceNotFoundException;
import fr.yoga.booking.service.business.security.annotation.CanListPlaces;
import fr.yoga.booking.service.business.security.annotation.CanRegisterPlace;
import fr.yoga.booking.service.business.security.annotation.CanUpdatePlace;
import fr.yoga.booking.service.business.security.annotation.CanViewPlace;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {
	private final PlaceRepository placeRepository;
	private final LessonRepository lessonRepository;
	private final ScheduledClassRepository scheduledClassRepository;

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
	
	@CanUpdatePlace
	public Place addMap(String placeId, Image image) throws PlaceException {
		Place place = getPlace(placeId);
		place.addMap(image);
		return updatePlace(place);
	}
	
	private Place updatePlace(Place place) {
		Place updated = placeRepository.save(place);
		for (Lesson lesson : lessonRepository.findAll()) {
			if (lesson.getPlace().isSame(place)) {
				lesson.setPlace(updated);
				lessonRepository.save(lesson);
				for (ScheduledClass scheduledClass : scheduledClassRepository.findByLessonAndStartAfter(lesson, Optional.empty())) {
					scheduledClass.setLesson(lesson);
					scheduledClassRepository.save(scheduledClass);
				}
			}
		}
		return updated;
	}
}
