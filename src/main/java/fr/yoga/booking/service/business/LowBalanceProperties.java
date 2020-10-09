package fr.yoga.booking.service.business;

import java.time.Duration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@Valid
@ConfigurationProperties("low-balance")
public class LowBalanceProperties {
	@NotNull
	private Integer remainingClasses;
	
	@NotNull
	private Duration monthCardRemainingDuration;
	
	@NotNull
	private Duration annualCardRemainingDuration;
}
