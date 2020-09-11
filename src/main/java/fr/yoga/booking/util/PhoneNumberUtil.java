package fr.yoga.booking.util;

import static java.util.regex.Pattern.quote;

public final class PhoneNumberUtil {
	public static String toSearchExpression(String phoneNumber) {
		if (isFrenchPhoneNumber(phoneNumber)) {
			return "^.+" + quote(lastCharacters(phoneNumber, 9)) + "$";
		}
		return quote(phoneNumber);
	}


	private static boolean isFrenchPhoneNumber(String phoneNumber) {
		return phoneNumber.startsWith("+33")
				|| phoneNumber.startsWith("+262")
				|| phoneNumber.length() == 10;
	}

	private static String lastCharacters(String phoneNumber, int numChars) {
		return phoneNumber.substring(phoneNumber.length() - numChars);
	}
}
