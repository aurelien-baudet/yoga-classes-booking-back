package fr.yoga.booking.service.business.security;

import static fr.yoga.booking.domain.account.Role.GOD;
import static fr.yoga.booking.domain.account.Role.TEACHER;
import static fr.yoga.booking.util.UserUtils.hasAnyRole;
import static fr.yoga.booking.util.UserUtils.isSameUser;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.Lesson;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessControlService {
	
	public boolean canViewUserInfo(User currentUser, String userId) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
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
	
	public boolean canViewPlace(User currentUser, String placeId) {
		return true;
	}

	public boolean canChangePlace(User currentUser, ScheduledClass scheduledClass) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can change place for a class of another teacher ?
		return false;
	}

	public boolean canChangeAllPlaces(User currentUser, Lesson lesson) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can change place for a class of another teacher ?
		return false;
	}

	public boolean canChangeTeacher(User currentUser, ScheduledClass scheduledClass) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}

	public boolean canChangeAllTeachers(User currentUser, Lesson lesson) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		return false;
	}
	
	public boolean canUpdatePlace(User currentUser, Place place) {
		if(hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// TODO: is any teacher can update place information ?
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
	
	public boolean canListBookedClasses(User currentUser, StudentRef student) {
		if (!student.isRegistered()) {
			return false;
		}
		return canListBookedClasses(currentUser, student.toStudent());
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
	
	public boolean canBookClass(User currentUser, ScheduledClass bookedClass, StudentRef student, User bookedBy) {
		return true;
	}
	
	public boolean canBookClass(User currentUser, ScheduledClass bookedClass, Student student, User bookedBy) {
		return true;
	}

	public boolean canBookClass(User currentUser, ScheduledClass bookedClass, UnregisteredUser student, User bookedBy) {
		return true;
	}
	
	public boolean canUnbookClass(User currentUser, ScheduledClass bookedClass, StudentRef student, User canceledBy) {
		// TODO: is anyone can unbook for anyone or only people who booked before ?
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

	public boolean canChangePasswordForStudent(User currentUser, Student student) {
		return hasAnyRole(currentUser, GOD);
	}

	public boolean canChangePasswordForTeacher(User currentUser, Teacher teacher) {
		return hasAnyRole(currentUser, GOD);
	}

	public boolean canTakeAvailablePlace(User currentUser, ScheduledClass bookedClass, StudentRef student, User bookedBy) {
		// anyone is allowed to take a place to a class if he has already booked it
		return bookedClass.allStudents().stream().anyMatch(s -> s.isSame(student));
	}
	
	public boolean canUpdateSubscriptionsForStudent(User currentUser, Student student) {
		return hasAnyRole(currentUser, GOD, TEACHER);
	}
	
	public boolean canListSubscriptions(User currentUser) {
		return hasAnyRole(currentUser, GOD, TEACHER);
	}
	
	public boolean canViewSubscriptionsForStudent(User currentUser, Student student) {
		if (hasAnyRole(currentUser, GOD, TEACHER)) {
			return true;
		}
		// can see his own subscriptions
		if(isSameUser(currentUser, student)) {
			return true;
		}
		return false;
	}

	public boolean canSendMessageToStudents(User currentUser, ScheduledClass scheduledClass) {
		// TODO: should also check that the teacher that sends the message is the teacher of the class ?
		return hasAnyRole(currentUser, GOD, TEACHER);
	}
	
	public boolean canListTeachers(User currentUser) {
		return true;
	}
	
	public boolean canUpdateProfile(User currentUser, Student student) {
		if(hasAnyRole(currentUser, GOD)) {
			return true;
		}
		// can update his own profile
		if(isSameUser(currentUser, student)) {
			return true;
		}
		return false;
	}

}
