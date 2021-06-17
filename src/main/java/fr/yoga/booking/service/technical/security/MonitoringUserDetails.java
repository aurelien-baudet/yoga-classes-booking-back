package fr.yoga.booking.service.technical.security;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class MonitoringUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3493620808386910609L;
	
	private final String username;
	private final String password;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return unmodifiableCollection(asList(new SimpleGrantedAuthority("MONITORING")));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
