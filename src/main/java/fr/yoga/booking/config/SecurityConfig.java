package fr.yoga.booking.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.yoga.booking.service.technical.security.AnonymousUserDetails;

@Configuration
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Configuration
	@ConditionalOnProperty(name="security.enabled", matchIfMissing=true)
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		UserDetailsService userDetailsService;
	
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService);
		}
	
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	//			.formLogin()
	//				.usernameParameter("login")
	//				.loginProcessingUrl("/users/login")
	//				.successHandler(new HttpStatusSuccessHandler(ACCEPTED))
	//				.failureHandler(new HttpStatusFailureHandler(UNAUTHORIZED))
	//				.and()
				// TODO: enable csrf
				// TODO: UnregisteredUser should be known in Spring Security in order to test security access ?	
				.csrf().disable()
				.cors().and()
				.httpBasic().and()
				.anonymous()
					.principal(new AnonymousUserDetails()).and()
				.authorizeRequests()
					.antMatchers(GET, "/users").permitAll()
					.antMatchers(GET, "/users/*").permitAll()
					.antMatchers(POST, "/users/students").permitAll()
					.antMatchers(POST, "/users/teachers").permitAll()
					.antMatchers(POST, "/users/unregistered").permitAll()
					.antMatchers(GET, "/users/*/preferences").permitAll()
					.antMatchers(GET, "/users/unregistered/*/preferences").permitAll()
					.antMatchers(GET, "/classes").permitAll()
					.antMatchers(GET, "/classes/*").permitAll()
					.antMatchers(GET, "/places").permitAll()
					// for unregistered users
					.antMatchers(GET, "/classes/bookings").permitAll()
					.antMatchers(POST, "/classes/*/bookings").permitAll()
					.antMatchers(DELETE, "/classes/*/bookings").permitAll()
					.anyRequest().authenticated().and()
		        ;
		}
	
	
		@Bean
		@ConfigurationProperties(prefix="cors")
		public CorsConfiguration corsConfiguration() {
			return new CorsConfiguration();
		}
	
		@Bean
		public CorsConfigurationSource corsConfigurationSource(CorsConfiguration corsProperties) {
			UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
			source.registerCorsConfiguration("/**", corsProperties);
			return source;
		}

	}
}
