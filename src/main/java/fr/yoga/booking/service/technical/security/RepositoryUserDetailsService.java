package fr.yoga.booking.service.technical.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.account.User;
import fr.yoga.booking.repository.StudentRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RepositoryUserDetailsService implements UserDetailsService {
	private final StudentRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findOneByAccountLogin(username);
		// TODO: if username is an email address, find user by email address
		if (user == null) {
			throw new UsernameNotFoundException("User with login '"+username+"' does'nt exist");
		}
		return new UserDetailsWrapper(user);
	}

}
