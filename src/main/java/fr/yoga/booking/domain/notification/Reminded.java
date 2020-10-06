package fr.yoga.booking.domain.notification;

import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Reminded {
	@Id
	private String id;
	private String scheduledClassId;
	private Instant scheduledClassStartDate;
	private Duration reminder;
	private Instant triggeredAt;
	
	public Reminded(Reminder reminder) {
		this(null, reminder.getScheduledClass().getId(), reminder.getScheduledClass().getStart(), reminder.getReminder(), now());
	}
}
