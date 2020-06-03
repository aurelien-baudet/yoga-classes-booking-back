package fr.yoga.booking.service.business.security;

import static fr.yoga.booking.domain.account.Role.GOD;
import static fr.yoga.booking.domain.account.Role.TEACHER;
import static fr.yoga.booking.util.UserUtils.hasAnyRole;
import static fr.yoga.booking.util.UserUtils.isSameUser;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensitiveInformationService {
	private final UserService userService;

	public StudentRef anonymize(StudentRef student) throws UserException {
		// TODO: if current user booked for friend and student is the friend => show info ?
		if(!student.isRegistered()) {
			// TODO: display name of unregistered users or anonymize by default ?
			return student;
		}
		User user = userService.getUser(student.getId());
		User currentUser = userService.getCurrentUser();
		// user can see its own name
		if (isSameUser(user, currentUser)) {
			return student;
		}
		// user can see other student name only if agreed
		if (visibleByOtherStudents(user)) {
			return student;
		}
		// teacher can see all names
		if (hasAnyRole(currentUser, GOD, TEACHER)) {
			return student;
		}
		return new StudentRef(student.getId(), "<anonyme>", student.isRegistered());
	}


	private boolean visibleByOtherStudents(User user) {
		if(!(user instanceof Student)) {
			return true;
		}
		return ((Student) user).getPreferences().isVisibleByOtherStudents();
	}

}
