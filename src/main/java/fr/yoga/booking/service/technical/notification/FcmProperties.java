package fr.yoga.booking.service.technical.notification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "fcm")
public class FcmProperties {
	private Resource serviceAccountFile;
}
