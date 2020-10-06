package fr.yoga.booking.service.business;

import java.time.Duration;
import java.util.SortedSet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("reminder")
public class ReminderProperties {
	private SortedSet<Duration> nextClass;
}
