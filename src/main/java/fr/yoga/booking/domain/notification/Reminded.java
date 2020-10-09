package fr.yoga.booking.domain.notification;

import static java.time.Instant.now;

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
	private String reminderId;
	private Instant triggeredAt;
	private Instant cleanableAt;
	private Instant remindedAt;
	
	public Reminded(Reminder<?> reminder) {
		this(null, reminder.getId(), reminder.getTriggerAt(), reminder.getCleanableAt(), now());
	}
}
