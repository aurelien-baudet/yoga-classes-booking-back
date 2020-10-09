package fr.yoga.booking.service.business;

import java.time.Duration;
import java.util.SortedSet;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Valid
@ConfigurationProperties("reminder")
public class ReminderProperties {
	@NotNull
	private NextClassProperties nextClass;
	@NotNull
	private SubscriptionProperties subscription;
	
	@Data
	public static class NextClassProperties {
		@NotNull
		private SortedSet<Duration> triggerBefore;
	}
	
	@Data
	public static class SubscriptionProperties {
		@NotNull
		private RemainingClassesProperties remainingClasses;
		@NotNull
		private PeriodCardProperties monthCard;
		@NotNull
		private PeriodCardProperties annualCard;
	}
	
	@Data
	public static class RemainingClassesProperties {
		@NotNull
		private SortedSet<Duration> triggerBeforeNextClass;
	}
	
	@Data
	public static class PeriodCardProperties {
		@NotNull
		private SortedSet<Duration> triggerBeforeNextClass;
		@NotNull
		private SortedSet<Duration> triggerBeforeExpiration;
	}
}
