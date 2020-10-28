package fr.yoga.booking.controller.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import fr.yoga.booking.domain.reservation.Image;
import fr.yoga.booking.domain.reservation.LessonDifficulty;
import fr.yoga.booking.domain.reservation.Place;
import fr.yoga.booking.domain.subscription.SubscriptionPack;
import lombok.Data;

@Data
public class LessonDto {
	private String id;
	private String title;
	private String description;
	private int maxStudents;
	private LessonDifficulty difficulty;
	private List<Image> photos = new ArrayList<>();
	private Place place;
	private boolean placeChanged;
	private TeacherDto teacher;
	@JsonFormat(shape = Shape.STRING)
	private SubscriptionPack subscriptionPack;
}
