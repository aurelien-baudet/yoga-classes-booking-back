package fr.yoga.booking.service.technical;

import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "deployment")
public class DeploymentContextProperties {
	private URL webAppBaseUrl;
	private String bookedClassPath = "classes/${id}";
	private String unbookPath = "classes/${id}?unbooking=${id}";
}
