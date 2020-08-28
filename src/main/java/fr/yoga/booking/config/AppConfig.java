package fr.yoga.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.yoga.booking.repository.ScheduledClassRepository;
import fr.yoga.booking.service.business.ConfirmBookingStrategy;
import fr.yoga.booking.service.business.FirstToConfirmTakesAvailablePlaceWaitingUserStrategy;
import fr.yoga.booking.service.business.NotificationService;
import fr.yoga.booking.service.business.WaitingListStrategy;

@Configuration
public class AppConfig {
	@Bean
	public WaitingListStrategy waitingListStrategy(ScheduledClassRepository scheduledClassRepository, NotificationService notificationService) {
		return firstToConfirm(scheduledClassRepository, notificationService);
	}

	@Bean
	public ConfirmBookingStrategy confirmStrategy(ScheduledClassRepository scheduledClassRepository, NotificationService notificationService) {
		return firstToConfirm(scheduledClassRepository, notificationService);
	}

	private FirstToConfirmTakesAvailablePlaceWaitingUserStrategy firstToConfirm(ScheduledClassRepository scheduledClassRepository, NotificationService notificationService) {
		return new FirstToConfirmTakesAvailablePlaceWaitingUserStrategy(scheduledClassRepository, notificationService);
	}
}
