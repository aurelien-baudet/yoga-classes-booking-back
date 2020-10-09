package fr.yoga.booking.domain.subscription;

import static fr.yoga.booking.util.DateUtil.isSameDay;
import static java.time.Instant.now;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodCard {
	private Instant start;
	private Instant end;
	
	public boolean expired() {
		return now().isAfter(end);
	}

	public boolean inProgress() {
		return started() && !expired();
	}

	private boolean started() {
		return now().isAfter(start) || isSameDay(start, now());
	}
}
