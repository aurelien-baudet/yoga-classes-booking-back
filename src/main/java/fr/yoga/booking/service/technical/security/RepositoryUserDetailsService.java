package fr.yoga.booking.service.technical.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RepositoryUserDetailsService implements UserDetailsService {
	private final StudentRepository userRepository;
	private final TeacherRepository teacherRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findStudentByLoginOrEmailAddress(username);
		if (user == null) {
			user = teacherRepository.findOneByAccountLogin(username);
		}
		if (user == null) {
			throw new UsernameNotFoundException("User with login '"+username+"' doesn't exist");
		}
		return new UserDetailsWrapper(user);
	}

	private User findStudentByLoginOrEmailAddress(String username) {
		User user = userRepository.findOneByAccountLogin(username);
		if (user == null) {
			user = userRepository.findOneByContactEmail(username);
		}
		return user;
	}

}
