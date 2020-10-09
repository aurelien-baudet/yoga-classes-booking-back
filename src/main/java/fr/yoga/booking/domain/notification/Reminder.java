package fr.yoga.booking.domain.notification;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reminder<T> {
	private final String id;
	private final T context;
	private final Instant triggerAt;
	private final Runnable task;
	private final Instant cleanableAt;
}
