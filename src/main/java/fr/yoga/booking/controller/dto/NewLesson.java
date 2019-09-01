package fr.yoga.booking.controller.dto;

import fr.yoga.booking.domain.reservation.LessonInfo;
import lombok.Data;

@Data
public class NewLesson {
	private LessonInfo info;
	private String placeId;
	private String teacherId;
}
