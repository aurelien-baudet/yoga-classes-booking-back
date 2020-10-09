package fr.yoga.booking.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.UpdatedSubscription;
import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.subscription.UserSubscriptions;
import fr.yoga.booking.service.business.SubscriptionService;
import fr.yoga.booking.service.business.UserService;
import fr.yoga.booking.service.business.exception.user.UserException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
	private final UserService userService;
	private final SubscriptionService subscriptionService;
	
	@GetMapping("{studentId}")
	public UserSubscriptions getSubscriptionsFor(@PathVariable String studentId) throws UserException {
		Student student = userService.getRegisteredStudent(studentId);
		return subscriptionService.getCurrentSubscriptionsFor(student);
	}
	
	@GetMapping
	public Page<UserSubscriptions> getSubscriptionsForAllStudents(Pageable page) {
		return subscriptionService.getSubscriptionsForAllStudents(page);
	}
	
	@PostMapping("{studentId}")
	public UserSubscriptions updateSubscriptionsForStudent(@PathVariable String studentId, @RequestBody UpdatedSubscription subscription) throws UserException {
		Student student = userService.getRegisteredStudent(studentId);
		return subscriptionService.updateSubscriptions(student, subscription.getRemainingClasses(), subscription.getMonthCard(), subscription.getAnnualCard());
	}
}
