package fr.yoga.booking.service.business;

import static fr.yoga.booking.domain.account.Role.STUDENT;
import static fr.yoga.booking.domain.account.Role.TEACHER;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Account;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Credentials;
import fr.yoga.booking.domain.account.Preferences;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.TeacherRepository;
import fr.yoga.booking.service.business.exception.AlreadyRegisteredUser;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.business.exception.user.StudentNotFoundException;
import fr.yoga.booking.service.business.exception.user.TeacherNotFoundException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.business.exception.user.UserNotFoundException;
import fr.yoga.booking.service.business.security.SecurityService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final SecurityService securityService;
	
	public User getUser(String userId) throws UserException {
		try {
			return getStudent(userId);
		} catch (StudentNotFoundException e) {
			// skip
		}
		try {
			return getTeacher(userId);
		} catch (TeacherNotFoundException e) {
			// skip
		}
		throw new UserNotFoundException(userId);
	}
	
	public Student getStudent(String studentId) throws UserException {
		return studentRepository.findById(studentId)
				.orElseThrow(() -> new StudentNotFoundException(studentId));
	}
	
	public Teacher getTeacher(String teacherId) throws UserException {
		return teacherRepository.findById(teacherId)
				.orElseThrow(() -> new TeacherNotFoundException(teacherId));
	}
	
	public Student registerStudent(String displayName, Credentials credentials, ContactInfo contact, Preferences preferences) throws AccountException {
		// TODO: check that email and phone number are not already used by someone else
		Student student = new Student(displayName, new Account(securityService.encodePassword(credentials), STUDENT), contact, preferences);
		// check that user doesn't already exist
		if(exists(student)) {
			throw new AlreadyRegisteredUser(student);
		}
		// store new user
		return studentRepository.save(student);
	}

	public Teacher registerTeacher(String displayName, Credentials credentials) throws AccountException {
		Teacher teacher = new Teacher(displayName, new Account(securityService.encodePassword(credentials), TEACHER));
		// check that user doesn't already exist
		if(exists(teacher)) {
			throw new AlreadyRegisteredUser(teacher);
		}
		// store new user
		return teacherRepository.save(teacher);
	}
	
	public StudentInfo anonymize(StudentInfo student) throws UserException {
		// TODO: if current user => show info ?
		// TODO: if current user booked for friend and student is the friend => show info ?
		if(!student.isRegistered()) {
			// TODO: display name of unregistered users or anonymize by default ?
			return student;
		}
		User user = getUser(student.getId());
		if(visibleByOtherStudents(user)) {
			return student;
		}
		return new StudentInfo(student.getId(), "<anonyme>", student.getEmail(), student.getPhoneNumber());
	}
	
	public boolean isLoginAvailable(String login) {
		boolean existsAsStudent = studentRepository.existsByAccountLogin(login);
		if(existsAsStudent) {
			return false;
		}
		boolean existsAsTeacher = teacherRepository.existsByAccountLogin(login);
		if(existsAsTeacher) {
			return false;
		}
		return true;
	}

	private boolean exists(User user) {
		boolean existsAsStudent = studentRepository.existsByAccountLogin(user.getAccount().getLogin());
		if(existsAsStudent) {
			return true;
		}
		boolean existsAsTeacher = teacherRepository.existsByAccountLogin(user.getAccount().getLogin());
		if(existsAsTeacher) {
			return true;
		}
		return false;
	}

	private boolean visibleByOtherStudents(User user) {
		if(!(user instanceof Student)) {
			return true;
		}
		return ((Student) user).getPreferences().isVisibleByOtherStudents();
	}
}
