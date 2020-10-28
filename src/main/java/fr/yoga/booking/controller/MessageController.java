package fr.yoga.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.yoga.booking.controller.dto.MessageData;
import fr.yoga.booking.controller.dto.SendReportDto;
import fr.yoga.booking.controller.mapper.SendReportMapper;
import fr.yoga.booking.domain.reservation.ScheduledClass;
import fr.yoga.booking.service.business.ClassService;
import fr.yoga.booking.service.business.NotificationService;
import fr.yoga.booking.service.business.exception.reservation.ScheduledClassException;

@RestController
@RequestMapping("messages")
public class MessageController {
	@Autowired
	ClassService classService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	SendReportMapper reportMapper;
	
	@PostMapping(path = "{classId}", params = "approved")
	public List<SendReportDto> sendMessageToApprovedStudents(@RequestBody MessageData message, @PathVariable("classId") String classId) throws ScheduledClassException {
		ScheduledClass scheduledClass = classService.getClass(classId);
		return reportMapper.toDto(notificationService.sendMessageToApprovedStudents(message.getMessage(), scheduledClass));
	}
}
