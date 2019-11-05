package fr.yoga.booking.service.business.security;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.UserException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensitiveInformationService {
	private final UserService userService;

	public StudentInfo anonymize(StudentInfo student) throws UserException {
		// TODO: if current user => show info ?
		// TODO: if current user booked for friend and student is the friend => show info ?
		if(!student.isRegistered()) {
			// TODO: display name of unregistered users or anonymize by default ?
			return student;
		}
		User user = userService.getUser(student.getId());
		if(visibleByOtherStudents(user)) {
			return student;
		}
		return new StudentInfo(student.getId(), "<anonyme>", student.getEmail(), student.getPhoneNumber());
	}


	private boolean visibleByOtherStudents(User user) {
		if(!(user instanceof Student)) {
			return true;
		}
		return ((Student) user).getPreferences().isVisibleByOtherStudents();
	}

}
