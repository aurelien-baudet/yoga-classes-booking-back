package fr.yoga.booking.domain.notification;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
	@Id
	private String id;
	private String scheduledClassId;
	private List<Instant> remindAt;
	
	public Reminder(ScheduledClass scheduledClass, Instant... remindAt) {
		this(null, scheduledClass.getId(), Arrays.asList(remindAt));
	}
}
