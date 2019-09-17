package fr.yoga.booking.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.DeviceInfo;
import fr.yoga.booking.service.business.NotificationService;
import fr.yoga.booking.service.technical.security.UserDetailsWrapper;

@RestController
@RequestMapping("devices")
public class DeviceController {
	@Autowired
	NotificationService notificationService;

	@PostMapping
	@ResponseStatus(NO_CONTENT)
	public void registerDeviceForCurrentUser(@AuthenticationPrincipal UserDetailsWrapper wrapper, @RequestBody DeviceInfo device) {
		if(wrapper == null) {
			return;
		}
		notificationService.registerNotificationTokenForUser(wrapper.getUser(), device.getFcmToken());
	}
}
