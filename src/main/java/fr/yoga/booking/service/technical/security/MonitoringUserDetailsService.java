package fr.yoga.booking.service.technical.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.codecentric.boot.admin.client.config.InstanceProperties;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MonitoringUserDetailsService implements UserDetailsService {
	private final InstanceProperties monitoringInstanceProperties;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String configuredName = monitoringInstanceProperties.getMetadata().get("user.name");
		String configuredPassword = monitoringInstanceProperties.getMetadata().get("user.password");
		if (configuredName == null || configuredPassword == null) {
			throw new UsernameNotFoundException("No username/password configured to allow external monitoring");
		}
		if (!configuredName.equals(username)) {
			throw new UsernameNotFoundException("User "+username+" is not allowed to monitor application");
		}
		return new MonitoringUserDetails(configuredName, passwordEncoder.encode(configuredPassword));
	}

}
