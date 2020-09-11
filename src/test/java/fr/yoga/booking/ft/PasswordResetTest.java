package fr.yoga.booking.ft;

import static fr.sii.ogham.testing.assertion.OghamAssertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.icegreen.greenmail.junit5.GreenMailExtension;

import fr.sii.ogham.testing.extension.junit.sms.JsmppServerExtension;
import fr.sii.ogham.testing.extension.spring.GreenMailInitializer;
import fr.sii.ogham.testing.extension.spring.JsmppServerInitializer;
import fr.yoga.booking.controller.PasswordController;
import fr.yoga.booking.controller.dto.ResetPassword;
import fr.yoga.booking.domain.account.ContactInfo;
import fr.yoga.booking.domain.account.Credentials;
import fr.yoga.booking.domain.account.Preferences;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.repository.StudentRepository;
import fr.yoga.booking.service.business.PasswordResetProperties;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.AccountException;
import fr.yoga.booking.service.business.exception.user.ExpiredResetTokenException;
import fr.yoga.booking.service.business.exception.user.InvalidResetTokenException;
import fr.yoga.booking.service.business.exception.user.PasswordResetException;
import fr.yoga.booking.service.technical.security.RandomGenerator;

@SpringBootTest(properties = {
	"security.enabled=false",
	"mail.smtp.host=127.0.0.1",
	"mail.smtp.port=${greenmail.smtp.port}",
	"ogham.sms.smpp.host=127.0.0.1",
	"ogham.sms.smpp.port=${jsmpp.server.port}",
})
@ActiveProfiles("test")
@ContextConfiguration(initializers = {GreenMailInitializer.class, JsmppServerInitializer.class})
class PasswordResetTest {
	@Autowired @RegisterExtension GreenMailExtension greenMail;
	@Autowired @RegisterExtension JsmppServerExtension smppServer;

	@Autowired PasswordController passwordController;
	@Autowired UserService userService;
	@Autowired StudentRepository studentRepository;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired PasswordResetProperties resetProperties;
	
	@MockBean RandomGenerator random;
	
	@BeforeEach
	void setup() throws AccountException {
		userService.registerStudent("Odile Deray", new Credentials("odile.deray", "foo"), new ContactInfo("odile.deray@yopmail.com", "+262601020304"), new Preferences());
		userService.registerStudent("Simon Jeremy 1", new Credentials("simon.jeremy.1", "foo"), new ContactInfo("simon.jeremy@yopmail.com", "+262600000000"), new Preferences());
		userService.registerStudent("Simon Jeremy 2", new Credentials("simon.jeremy.2", "foo"), new ContactInfo("simon.jeremy@yopmail.com", "+262600000000"), new Preferences());
	}
	
	@AfterEach
	void cleanup() {
		studentRepository.deleteAll();
	}
	
	@Test
	void asStudentIResetMyPasswordUsingMyEmailAddress() throws PasswordResetException {
		String token = "token-1";
		when(random.generate(anyInt())).thenReturn(token);

		passwordController.requestPasswordReset("odile.deray@yopmail.com");
		assertEmailReceived("odile.deray@yopmail.com", token);

		passwordController.validateResetCode(token);
		passwordController.confirmPasswordReset(new ResetPassword(token, "bar"));
		assertAuthSucceeds("odile.deray", "bar");
		assertAuthFails("odile.deray", "foo");
	}
	
	@Test
	void asStudentIResetMyPasswordUsingMyNationalPhoneNumber() throws PasswordResetException {
		String token = "token-2";
		when(random.generate(anyInt())).thenReturn(token);

		passwordController.requestPasswordReset("0601020304");
		assertSmsReceived("+262601020304", token);

		passwordController.validateResetCode(token);
		passwordController.confirmPasswordReset(new ResetPassword(token, "bar"));
		assertAuthSucceeds("odile.deray", "bar");
		assertAuthFails("odile.deray", "foo");
	}

	@Test
	void asStudentIResetMyPasswordUsingMyInationalPhoneNumber() throws PasswordResetException {
		String token = "token-3";
		when(random.generate(anyInt())).thenReturn(token);

		passwordController.requestPasswordReset("+262601020304");
		assertSmsReceived("+262601020304", token);

		passwordController.validateResetCode(token);
		passwordController.confirmPasswordReset(new ResetPassword(token, "bar"));
		assertAuthSucceeds("odile.deray", "bar");
		assertAuthFails("odile.deray", "foo");
	}

	@Test
	void asHackerITryToFindAccountUsingEmailButNoInformationIsRevealed() throws PasswordResetException {
		passwordController.requestPasswordReset("unexisting@yopmail.com");
		assertThat(greenMail).receivedMessages().count(is(0));
	}

	@Test
	void asHackerITryToFindAccountUsingPhoneNumberButNoInformationIsRevealed() throws PasswordResetException {
		passwordController.requestPasswordReset("0700000000");
		assertThat(smppServer).receivedMessages().count(is(0));
	}

	@Test
	void asStudentWithSeveralAccountsWithSameEmailIResetMyPasswordUsingMyEmailAddress() throws PasswordResetException {
		String token1 = "token-4a";
		String token2 = "token-4b";
		when(random.generate(anyInt())).thenReturn(token1, token2);

		passwordController.requestPasswordReset("simon.jeremy@yopmail.com");
		assertEmailReceived("simon.jeremy@yopmail.com", token1, token2);

		passwordController.validateResetCode(token1);
		passwordController.confirmPasswordReset(new ResetPassword(token1, "bar"));
		assertAuthSucceeds("simon.jeremy.1", "bar");
		assertAuthFails("simon.jeremy.1", "foo");
		assertAuthSucceeds("simon.jeremy.2", "foo");
		assertAuthFails("simon.jeremy.2", "bar");
	}

	@Test
	void asStudentWithSeveralAccountsWithSamePhoneNumberIResetMyPasswordUsingMyPhoneNumber() throws PasswordResetException {
		String token1 = "token-5a";
		String token2 = "token-5b";
		when(random.generate(anyInt())).thenReturn(token1, token2);

		passwordController.requestPasswordReset("+262600000000");
		assertSmsReceived("+262600000000", token1, token2);
		
		passwordController.validateResetCode(token1);
		passwordController.confirmPasswordReset(new ResetPassword(token1, "bar"));
		assertAuthSucceeds("simon.jeremy.1", "bar");
		assertAuthFails("simon.jeremy.1", "foo");
		assertAuthSucceeds("simon.jeremy.2", "foo");
		assertAuthFails("simon.jeremy.2", "bar");
	}

	@Test
	void asStudentICantReuseSameTokenTwice() throws PasswordResetException {
		String token = "token-6";
		when(random.generate(anyInt())).thenReturn(token);

		passwordController.requestPasswordReset("odile.deray@yopmail.com");
		assertEmailReceived("odile.deray@yopmail.com", token);

		passwordController.validateResetCode(token);
		passwordController.confirmPasswordReset(new ResetPassword(token, "bar"));
		assertAuthSucceeds("odile.deray", "bar");
		assertAuthFails("odile.deray", "foo");
		
		assertThrows(InvalidResetTokenException.class, () -> {
			passwordController.validateResetCode(token);
		});
	}

	@Test
	void asStudentICantResetPasswordIfTokenIsExpired() throws PasswordResetException, InterruptedException {
		String token = "token-7";
		when(random.generate(anyInt())).thenReturn(token);

		passwordController.requestPasswordReset("odile.deray@yopmail.com");
		assertEmailReceived("odile.deray@yopmail.com", token);

		Thread.sleep(resetProperties.getTokenValidity().toMillis() + 500);
		
		assertThrows(ExpiredResetTokenException.class, () -> {
			passwordController.validateResetCode(token);
		});
	}

	private void assertEmailReceived(String email, String token) {
		assertThat(greenMail).receivedMessages()
			.count(is(1))
			.message(0)
				.to().address(hasItem(email)).and()
				.body().contentAsString(containsString(token));
	}

	private void assertEmailReceived(String email, String token1, String token2) {
		assertThat(greenMail).receivedMessages()
			.count(is(2))
			.message(0)
				.to().address(hasItem(email)).and()
				.body().contentAsString(containsString(token1)).and()
				.and()
			.message(1)
				.to().address(hasItem(email)).and()
				.body().contentAsString(containsString(token2));
	}

	private void assertSmsReceived(String phoneNumber, String token) {
		assertThat(smppServer).receivedMessages()
			.count(is(1))
			.message(0)
				.to().number(is(phoneNumber)).and()
				.content(containsString(token));
	}

	private void assertSmsReceived(String phoneNumber, String token1, String token2) {
		assertThat(smppServer).receivedMessages()
			.count(is(2))
			.message(0)
				.to().number(is(phoneNumber)).and()
				.content(containsString(token1))
				.and()
			.message(1)
				.to().number(is(phoneNumber)).and()
				.content(containsString(token2));
	}

	private void assertAuthSucceeds(String login, String password) {
		Student student = studentRepository.findOneByAccountLogin(login);
		assertTrue(passwordEncoder.matches(password, student.getAccount().getPassword()));
	}

	private void assertAuthFails(String login, String password) {
		Student student = studentRepository.findOneByAccountLogin(login);
		assertFalse(passwordEncoder.matches(password, student.getAccount().getPassword()));
	}

	// TODO: other phone number
}
