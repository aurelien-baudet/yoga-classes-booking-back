package fr.yoga.booking.util;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.Instant;
import java.time.ZoneId;

public class DateRangeUtil {
	public static String format(Instant start, Instant end) {
		return start.atZone(ZoneId.systemDefault()).format(ofPattern("EEEE d MMMM H'h'mm")) + "-" + end.atZone(ZoneId.systemDefault()).format(ofPattern("H'h'mm"));
	}
}
