package fr.yoga.booking.service.technical;

import static fr.yoga.booking.util.ExpressionParser.evaluate;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.stereotype.Service;

import fr.yoga.booking.domain.reservation.ScheduledClass;
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
}
