package fr.yoga.booking.service.technical.notification;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("onesignal")
public class OneSignalProperties {
	private String apiUrl = "https://onesignal.com/api/v1/notifications";
	private String apiKey;
	private String appId;
	private Map<String, String> templates;
}
