package fr.yoga.booking.service.business;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties("scheduling")
public class SchedulingProperties {
	private boolean enableClassEvents = true;
}
