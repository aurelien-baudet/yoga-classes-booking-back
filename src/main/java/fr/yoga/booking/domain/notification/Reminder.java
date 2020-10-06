package fr.yoga.booking.domain.notification;

import static java.time.Instant.now;

import java.time.Duration;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
	private ScheduledClass scheduledClass;
	private Duration reminder;
	
	public boolean isNowBetweenReminderAndStartOfClass() {
		return now().isAfter(scheduledClass.getStart().minus(reminder)) 
				&& now().isBefore(scheduledClass.getStart());
	}
	
}
