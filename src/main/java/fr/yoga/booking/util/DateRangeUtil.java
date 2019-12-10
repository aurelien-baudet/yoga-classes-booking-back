package fr.yoga.booking.util;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.Instant;
import java.time.ZoneId;

import fr.yoga.booking.domain.reservation.ScheduledClass;

public class DateRangeUtil {
	public static String format(ScheduledClass scheduledClass) {
		return format(scheduledClass.getStart(), scheduledClass.getEnd());
	}
	
	public static String format(Instant start, Instant end) {
		return start.atZone(ZoneId.systemDefault()).format(ofPattern("EEEE d MMMM H'h'mm")) + "-" + end.atZone(ZoneId.systemDefault()).format(ofPattern("H'h'mm"));
	}

	public static String formatShort(ScheduledClass scheduledClass) {
		return formatShort(scheduledClass.getStart(), scheduledClass.getEnd());
	}
	
	public static String formatShort(Instant start, Instant end) {
		return start.atZone(ZoneId.systemDefault()).format(ofPattern("E d MMM H'h'mm")) + "-" + end.atZone(ZoneId.systemDefault()).format(ofPattern("H'h'mm"));
	}
}
