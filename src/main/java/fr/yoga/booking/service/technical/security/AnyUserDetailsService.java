package fr.yoga.booking.service.technical.security;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AnyUserDetailsService implements UserDetailsService {
	private final List<UserDetailsService> delegates;

	@Autowired
	public AnyUserDetailsService(List<UserDetailsService> delegates) {
		super();
		this.delegates = delegates;
	}
	
	public AnyUserDetailsService(UserDetailsService... delegates) {
		this(asList(delegates));
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		for (UserDetailsService service : delegates) {
			if (service == null) {
				continue;
			}
			try {
				return service.loadUserByUsername(username);
			} catch (UsernameNotFoundException e) {
				// try next one
			}
		}
		throw new UsernameNotFoundException("User with login '"+username+"' doesn't exist");
	}

}
