package fr.yoga.booking.service.technical.security;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.yoga.booking.domain.account.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDetailsWrapper implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final User user;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getAccount().getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.name()))
				.collect(toSet());
	}

	@Override
	public String getPassword() {
		return user.getAccount().getPassword();
	}

	@Override
	public String getUsername() {
		return user.getAccount().getLogin();
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
