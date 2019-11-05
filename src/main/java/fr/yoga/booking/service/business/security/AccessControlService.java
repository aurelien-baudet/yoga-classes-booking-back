package fr.yoga.booking.service.business.security;

import static fr.yoga.booking.domain.account.Role.GOD;
import static fr.yoga.booking.domain.account.Role.TEACHER;
import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Role;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessControlService {
	
	public boolean canViewUserInfo(User currentUser, String userId) {
		if(hasAnyRole(currentUser, GOD)) {
			return true;
		}
		// can see his own profile
		if(isSameUser(currentUser, userId)) {
			return true;
		}
		return false;
	}
	
	public boolean canViewStudentInfo(User currentUser, String studentId) {
		return canViewUserInfo(currentUser, studentId);
	}
	
	public boolean canViewTeacherInfo(User currentUser, String teacherId) {
		return canViewUserInfo(currentUser, teacherId);
	}
	
	public boolean canRegisterStudent(User currentUser) {
		// anyone can register himself
		return true;
	}
	
	public boolean canRegisterTeacher(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canCheckLoginAvailability(User currentUser, String login) {
		// TODO: prevent someone to test all used logins
		return true;
	}
	
	public boolean canRegisterLesson(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canListLessons(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canListFutureClasses(User currentUser) {
		return true;
	}
	
	public boolean canListClassesForLesson(User currentUser, Lesson lesson) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can list classes for a lesson of another teacher ?
		return false;
	}
	
	public boolean canViewLessonInfo(User currentUser, String lessonId) {
		return true;
	}
	
	public boolean canViewClassInfo(User currentUser, String classId) {
		return true;
	}
	
	public boolean canUpdateLessonInfo(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canRegisterPlace(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canListPlaces(User currentUser) {
		return true;
	}
	
	public boolean canChangePlace(User currentUser, ScheduledClass scheduledClass) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can change place for a class of another teacher ?
		return false;
	}
	
	public boolean canScheduleClass(User currentUser, Lesson lesson) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can schedule a lesson of another teacher ?
		return false;
	}
	
	public boolean canCancelClass(User currentUser, ScheduledClass scheduledClass) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can schedule a lesson of another teacher ?
		return false;
	}
	
	public boolean canListUnscheduledLessons(User currentUser) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canListBookedClasses(User currentUser, Student student) {
		if(hasAnyRole(currentUser, GOD)) {
			return true;
		}
		// can see his own bookings
		if(isSameUser(currentUser, student)) {
			return true;
		}
		return false;
	}

	public boolean canListBookedClasses(User currentUser, UnregisteredUser student) {
		if(hasAnyRole(currentUser, GOD)) {
			return true;
		}
		// TODO: can see his own bookings
//		if(isSameUser(currentUser, userId)) {
//			return true;
//		}
		// TODO: should not be able to see booked classes of any unregistered user
		return true;
	}
	
	public boolean canBookClass(User currentUser, ScheduledClass bookedClass, Student student, User bookedBy) {
		return true;
	}

	public boolean canBookClass(User currentUser, ScheduledClass bookedClass, UnregisteredUser student, User bookedBy) {
		return true;
	}
	
	public boolean canUnbookClass(User currentUser, ScheduledClass bookedClass, Student student, User canceledBy) {
		// TODO: is anyone can unbook for anyone or only people who booked before ?
		return true;
	}

	public boolean canUnbookClass(User currentUser, ScheduledClass bookedClass, UnregisteredUser student, User canceledBy) {
		// TODO: is anyone can unbook for anyone or only people who booked before ?
		return true;
	}

	public boolean canListApprovedBookings(User currentUser) {
		return true;
	}
	
	public boolean canListWaitingBookings(User currentUser) {
		return true;
	}

	public boolean canRegisterNotificationToken(User currentUser, User forUser) {
		return true;
	}

	public boolean canUnregisterNotificationToken(User currentUser, User forUser) {
		return true;
	}
	
	private boolean hasAnyRole(User user, Role... anyRole) {
		if(user == null) {
			return false;
		}
		List<Role> roles = asList(anyRole);
		return user.getAccount().getRoles()
				.stream()
				.anyMatch(roles::contains);
	}
	
	private boolean isSameUser(User a, User b) {
		if(a == null || b == null) {
			return false;
		}
		return a.isSame(b);
	}
	
	private boolean isSameUser(User user, String userId) {
		if(user == null || userId == null) {
			return false;
		}
		return user.isSame(userId);
	}
}
