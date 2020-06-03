package fr.yoga.booking.service.business;

import static fr.yoga.booking.domain.account.Role.STUDENT;
import static fr.yoga.booking.domain.account.Role.TEACHER;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.account.Account;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Credentials;
import fr.yoga.booking.domain.account.Preferences;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.UnregisteredUserPreferences;
import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.domain.reservation.StudentRef;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.TeacherRepository;
import fr.yoga.booking.repository.UnregisteredUserRepository;
import fr.yoga.booking.service.business.exception.AlreadyRegisteredUser;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.business.exception.user.StudentNotFoundException;
import fr.yoga.booking.service.business.exception.user.TeacherNotFoundException;
import fr.yoga.booking.service.business.exception.user.UnregisteredUserNotFoundException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.business.exception.user.UserNotFoundException;
import fr.yoga.booking.service.business.security.annotation.CanCheckLoginAvailability;
import fr.yoga.booking.service.business.security.annotation.CanRegisterStudent;
import fr.yoga.booking.service.business.security.annotation.CanRegisterTeacher;
import fr.yoga.booking.service.business.security.annotation.CanViewStudentInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewTeacherInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewUserInfo;
import fr.yoga.booking.service.technical.security.PasswordService;
import fr.yoga.booking.service.technical.security.UserDetailsWrapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final StudentRepository studentRepository;
	private final UnregisteredUserRepository unregisteredUserRepository;
	private final TeacherRepository teacherRepository;
	private final PasswordService securityService;
	
	@CanViewUserInfo
	public User getUserInfo(String userId) throws UserException {
		return getUser(userId);
	}
	
	@CanViewStudentInfo
	public Student getRegisteredStudent(String studentId) throws UserException {
		return studentRepository.findById(studentId)
				.orElseThrow(() -> new StudentNotFoundException(studentId));
	}
	
	@CanViewStudentInfo
	public UnregisteredUser getUnregisteredStudent(String studentId) throws UserException {
		return unregisteredUserRepository.findById(studentId)
				.orElseThrow(() -> new UnregisteredUserNotFoundException(studentId));
	}
	
	@CanViewTeacherInfo
	public Teacher getTeacher(String teacherId) throws UserException {
		return teacherRepository.findById(teacherId)
				.orElseThrow(() -> new TeacherNotFoundException(teacherId));
	}
	
	@CanRegisterStudent
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

	// TODO: secure ?
	public UnregisteredUser saveUnregisteredUserInfo(String displayName, ContactInfo contact, UnregisteredUserPreferences preferences) {
		UnregisteredUser unregisteredUser = new UnregisteredUser(displayName, contact, preferences);
		return unregisteredUserRepository.save(unregisteredUser);
	}

	@CanRegisterTeacher
	public Teacher registerTeacher(String displayName, Credentials credentials) throws AccountException {
		Teacher teacher = new Teacher(displayName, new Account(securityService.encodePassword(credentials), TEACHER));
		// check that user doesn't already exist
		if(exists(teacher)) {
			throw new AlreadyRegisteredUser(teacher);
		}
		// store new user
		return teacherRepository.save(teacher);
	}
	
	@CanCheckLoginAvailability
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

	// TODO: secure ?
	public StudentRef getStudentRef(String studentId) throws UserException {
		try {
			return new StudentRef(getRegisteredStudent(studentId));
		} catch (StudentNotFoundException e) {
			// skip
		}
		try {
			return new StudentRef(getUnregisteredStudent(studentId));
		} catch (UnregisteredUserNotFoundException e) {
			// skip
		}
		throw new StudentNotFoundException(studentId);
	}
	
	// TODO: secure ?
	public ContactInfo getContactInfo(StudentRef student) throws UserException {
		return getContactInfo(student.getId());
	}

	// TODO: secure ?
	public ContactInfo getContactInfo(String studentId) throws UserException {
		try {
			return getRegisteredStudent(studentId).getContact();
		} catch (StudentNotFoundException e) {
			// skip
		}
		try {
			return getUnregisteredStudent(studentId).getContact();
		} catch (UnregisteredUserNotFoundException e) {
			// skip
		}
		throw new StudentNotFoundException(studentId);
	}
	
	// TODO: secure ?
	public User getUser(String userId) throws UserException {
		try {
			return getRegisteredStudent(userId);
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
	
	// TODO: secure ?
	public User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetailsWrapper) {
			return ((UserDetailsWrapper) principal).getUser();
		}
		return null;
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
}
