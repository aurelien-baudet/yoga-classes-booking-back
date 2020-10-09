package fr.yoga.booking.domain.notification;

import fr.yoga.booking.domain.subscription.UserSubscriptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RenewAnnualCardNotification implements Notification {
	private final UserSubscriptions subscription;
	
	@Override
	public NotificationType getType() {
		return NotificationType.RENEW_ANNUAL_CARD;
	}
}
