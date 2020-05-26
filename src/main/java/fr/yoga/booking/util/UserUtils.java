package fr.yoga.booking.util;

import static java.util.Arrays.asList;

import java.util.List;

import fr.yoga.booking.domain.account.Role;
import fr.yoga.booking.domain.account.User;

public class UserUtils {
	
	public static boolean hasAnyRole(User user, Role... anyRole) {
		if(user == null) {
			return false;
		}
		List<Role> roles = asList(anyRole);
		return user.getAccount().getRoles()
				.stream()
				.anyMatch(roles::contains);
	}
	
	public static boolean isSameUser(User a, User b) {
		if(a == null || b == null) {
			return false;
		}
		return a.isSame(b);
	}
	
	public static boolean isSameUser(User user, String userId) {
		if(user == null || userId == null) {
			return false;
		}
		return user.isSame(userId);
	}
}
