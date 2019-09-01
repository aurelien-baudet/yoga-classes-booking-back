package fr.yoga.booking.controller.dto;

import java.util.ArrayList;
import java.util.List;

import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.domain.reservation.Photo;
import fr.yoga.booking.domain.reservation.Place;
import lombok.Data;

@Data
public class LessonDto {
	private String id;
	private String title;
	private String description;
	private int maxStudents;
	private List<Photo> photos = new ArrayList<>();
	private Place place;
	private Teacher teacher;
}
