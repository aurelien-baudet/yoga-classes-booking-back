package fr.yoga.booking.service.business;

import static fr.yoga.booking.domain.account.Role.STUDENT;
import static fr.yoga.booking.domain.account.Role.TEACHER;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.sii.ogham.core.exception.MessagingException;
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
import fr.yoga.booking.service.business.exception.user.PasswordResetException;
import fr.yoga.booking.service.business.exception.user.StudentNotFoundException;
import fr.yoga.booking.service.business.exception.user.TeacherNotFoundException;
import fr.yoga.booking.service.business.exception.user.UnregisteredUserNotFoundException;
import fr.yoga.booking.service.business.exception.user.UserException;
import fr.yoga.booking.service.business.exception.user.UserNotFoundException;
import fr.yoga.booking.service.business.security.annotation.CanChangePasswordForStudent;
import fr.yoga.booking.service.business.security.annotation.CanCheckLoginAvailability;
import fr.yoga.booking.service.business.security.annotation.CanRegisterStudent;
import fr.yoga.booking.service.business.security.annotation.CanRegisterTeacher;
import fr.yoga.booking.service.business.security.annotation.CanViewStudentInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewTeacherInfo;
import fr.yoga.booking.service.business.security.annotation.CanViewUserInfo;
import fr.yoga.booking.service.technical.security.PasswordService;
import fr.yoga.booking.service.technical.security.TokenService;
import fr.yoga.booking.service.technical.security.UserDetailsWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final StudentRepository studentRepository;
	private final UnregisteredUserRepository unregisteredUserRepository;
	private final TeacherRepository teacherRepository;
	private final PasswordService passwordService;
	private final TokenService tokenService;
	private final ContactService contactService;
	
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
		Student student = new Student(displayName, new Account(passwordService.encodePassword(credentials), STUDENT), contact, preferences);
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
		Teacher teacher = new Teacher(displayName, new Account(passwordService.encodePassword(credentials), TEACHER));
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
	
//	// TODO: secure ?
//	public ContactInfo getContactInfo(StudentRef student) throws UserException {
//		return getContactInfo(student.getId());
//	}
//
//	// TODO: secure ?
//	public ContactInfo getContactInfo(String studentId) throws UserException {
//		try {
//			return getRegisteredStudent(studentId).getContact();
//		} catch (StudentNotFoundException e) {
//			// skip
//		}
//		try {
//			return getUnregisteredStudent(studentId).getContact();
//		} catch (UnregisteredUserNotFoundException e) {
//			// skip
//		}
//		throw new StudentNotFoundException(studentId);
//	}
	
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
	

	public void requestPasswordReset(String emailOrPhoneNumber) throws PasswordResetException {
		List<Student> matches = studentRepository.findByEmailOrPhoneNumber(emailOrPhoneNumber);
		if (matches.isEmpty()) {
			log.warn("[reset-password] No user found for email address or phone number '{}'", emailOrPhoneNumber);
		}
		for (Student student : matches) {
			try {
				String token = tokenService.generateResetToken(student, emailOrPhoneNumber);
				contactService.sendResetPasswordMessage(student, emailOrPhoneNumber, token);
			} catch (MessagingException e) {
				log.error("Failed to send message to reset password", e);
			}
		}
	}
	
	public void validateResetToken(String token) throws PasswordResetException {
		tokenService.validateResetToken(token);
	}
	
	public void resetPassword(String token, String newPassword) throws PasswordResetException {
		User user = tokenService.validateResetToken(token);
		try {
			Student student = getRegisteredStudent(user.getId());
			changePassword(student, newPassword);
			tokenService.invalidateResetToken(token);
		} catch (UserException e) {
			throw new PasswordResetException("Failed to reset password for "+user.getDisplayName(), e);
		}
	}
	
	
	@CanChangePasswordForStudent
	public void changePassword(Student student, String newPassword) throws PasswordResetException {
		student.getAccount().setPassword(passwordService.encodePassword(newPassword));
		studentRepository.save(student);
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
