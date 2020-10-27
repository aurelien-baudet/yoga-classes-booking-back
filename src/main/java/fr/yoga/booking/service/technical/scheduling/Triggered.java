package fr.yoga.booking.service.technical.scheduling;

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
public class Triggered {
	@Id
	private String id;
	private String triggerId;
	private String triggerType;
	private Instant triggerAt;
	private Instant cleanableAt;
	private String contextId;
	private Instant triggeredAt;
	
	public Triggered(Trigger<?> trigger) {
		this(null, trigger.getId(), trigger.getType(), trigger.getTriggerAt(), trigger.getCleanableAt(), trigger.getContextId(), now());
	}
}
