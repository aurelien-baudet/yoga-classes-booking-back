package fr.yoga.booking.service.business;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	public void classCanceled(ScheduledClass scheduledClass) {
		
	}

	public void placeChanged(ScheduledClass scheduledClass, Place oldPlace, Place newPlace) {
		
	}

	public void booked(ScheduledClass bookedClass, StudentInfo student, User bookedBy) {
		
	}

	public void bookingCanceled(ScheduledClass bookedClass, StudentInfo student, User canceledBy) {
		
	}

}
