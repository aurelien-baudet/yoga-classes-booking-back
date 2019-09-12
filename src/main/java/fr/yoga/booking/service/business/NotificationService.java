package fr.yoga.booking.service.business;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.Booking;
import fr.yoga.booking.domain.reservation.CancelData;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

	public void classCanceled(ScheduledClass scheduledClass, CancelData addtionalInfo) {
		log.info("[{}] class canceled. Message: {}", scheduledClass.getId(), addtionalInfo.getMessage());
		
	}

	public void placeChanged(ScheduledClass scheduledClass, Place oldPlace, Place newPlace) {
		log.info("[{}] place changed {} -> {}", scheduledClass.getId(), oldPlace.getName(), newPlace.getName());
		
	}

	public void booked(ScheduledClass bookedClass, StudentInfo student, User bookedBy) {
		log.info("[{}] booked for {}", bookedClass.getId(), student.getDisplayName());
		
	}

	public void unbooked(ScheduledClass bookedClass, StudentInfo student, User canceledBy) {
		log.info("[{}] unbooked for {}", bookedClass.getId(), student.getDisplayName());
		
	}

	public void freePlaceBooked(ScheduledClass scheduledClass, Booking firstWaiting) {
		log.info("[{}] place available => automatically booked for {}", scheduledClass.getId(), firstWaiting.getStudent().getDisplayName());
		
	}

}
