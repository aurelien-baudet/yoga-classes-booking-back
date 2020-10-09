package fr.yoga.booking.domain.subscription;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.reservation.StudentRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptions {
	@Id
	private String id;
	private StudentRef subscriber;
	private Instant date;
	private int remainingClasses;
	private PeriodCard monthCard;
	private PeriodCard annualCard;
	
	public UserSubscriptions(Student subscriber) {
		this(subscriber, 0, null, null);
	}
	
	public UserSubscriptions(Student subscriber, int remainingClasses, PeriodCard monthCard, PeriodCard annualCard) {
		this(null, new StudentRef(subscriber), Instant.now(), remainingClasses, monthCard, annualCard);
	}

	public boolean hasValidMonthCard() {
		if (monthCard == null) {
			return false;
		}
		return monthCard.inProgress();
	}

	public boolean hasValidAnnualCard() {
		if (annualCard == null) {
			return false;
		}
		return annualCard.inProgress();
	}

	public void decreasePaid(int numPaid) {
		remainingClasses -= numPaid;
	}

	@Transient
	public int getUnpaidClasses() {
		return Math.max(-remainingClasses, 0);
	}
}
