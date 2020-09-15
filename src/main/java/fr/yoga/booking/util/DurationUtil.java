package fr.yoga.booking.util;

import java.time.Duration;

import org.apache.commons.lang3.time.DurationFormatUtils;

public final class DurationUtil {
	public static String format(Duration duration) {
		String formattedInEnglish = DurationFormatUtils.formatDurationWords(duration.toMillis(), true, true);
		return formattedInEnglish.replace("day", "jour").replace("hour", "heure").replace("second", "seconde");
	}
}
