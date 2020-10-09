package fr.yoga.booking.util;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.Instant;
import java.time.ZoneId;

public final class DateUtil {
	public static Instant startOfDay(Instant date) {
		ZoneId zone = ZoneId.systemDefault();
		return date.atZone(zone).toLocalDate().atStartOfDay(zone).toInstant();
	}
	
	public static Instant endOfDay(Instant date) {
		ZoneId zone = ZoneId.systemDefault();
		return date.atZone(zone).toLocalDate().plusDays(1).atStartOfDay(zone).toInstant().minusMillis(1);
	}

	public static Instant midday(Instant date) {
		ZoneId zone = ZoneId.systemDefault();
		return date.atZone(zone).toLocalDate().atStartOfDay(zone).plusHours(12).toInstant();
	}
	
	public static String formatDate(Instant date) {
		return format(date, "dd/MM/yyyy");
	}

	public static String format(Instant date, String pattern) {
		return date.atZone(ZoneId.systemDefault()).format(ofPattern(pattern));
	}

	public static boolean isSameDay(Instant a, Instant b) {
		ZoneId zone = ZoneId.systemDefault();
		return a.atZone(zone).toLocalDate().isEqual(b.atZone(zone).toLocalDate());
	}

	private DateUtil() {
		super();
	}
}
