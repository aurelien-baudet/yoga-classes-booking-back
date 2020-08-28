package fr.yoga.booking.service.technical;

import static fr.yoga.booking.util.ExpressionParser.evaluate;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Service
@RequiredArgsConstructor
public class DeploymentContextService {
	private final DeploymentContextProperties deployment;

	public URL viewClassUrl(ScheduledClass scheduledClass) throws MalformedURLException {
		return new URL(deployment.getWebAppBaseUrl(), evaluate(deployment.getBookedClassPath(), scheduledClass));
	}
	
	public URL unbookUrl(ScheduledClass scheduledClass) throws MalformedURLException {
		return new URL(deployment.getWebAppBaseUrl(), evaluate(deployment.getUnbookPath(), scheduledClass));
	}
	
	public URL takeAvailablePlaceUrl(ScheduledClass scheduledClass) throws MalformedURLException {
		return new URL(deployment.getWebAppBaseUrl(), evaluate(deployment.getTakeAvailablePlacePath(), scheduledClass));
	}
	
	public URL unsubscribeEmailsUrl(StudentRef student) throws MalformedURLException {
		return new URL(deployment.getWebAppBaseUrl(), evaluate(deployment.getUnsubscribeEmailsPath(), student));
	}
	
	public URL resetPasswordUrl(String token) throws MalformedURLException {
		return new URL(deployment.getWebAppBaseUrl(), evaluate(deployment.getResetPasswordPath(), new TokenWrapper(token)));
	}

	@Data
	public static class TokenWrapper {
		private final String token;
	}
}
