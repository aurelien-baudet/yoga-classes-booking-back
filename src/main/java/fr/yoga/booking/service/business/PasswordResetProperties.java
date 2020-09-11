package fr.yoga.booking.service.business;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("password-reset")
public class PasswordResetProperties {
	private Duration tokenValidity;
	private Integer tokenLength;
}
