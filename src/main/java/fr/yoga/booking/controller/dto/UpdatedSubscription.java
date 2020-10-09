package fr.yoga.booking.controller.dto;

import fr.yoga.booking.domain.subscription.PeriodCard;
import lombok.Data;

@Data
public class UpdatedSubscription {
	private int remainingClasses;
	private PeriodCard monthCard;
	private PeriodCard annualCard;
}
