package fr.yoga.booking.domain.reservation;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import fr.yoga.booking.domain.subscription.SubscriptionPack;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonInfo {
	private String title;
	private String description;
	private int maxStudents;
	private List<Image> photos;
	private LessonDifficulty difficulty;
	@JsonFormat(shape = Shape.STRING)
	private SubscriptionPack subscriptionPack; 
	
	public LessonInfo() {
		this(null, null, 0, new ArrayList<>(), new LessonDifficulty(), SubscriptionPack.STANDARD);
	}
	
	public LessonInfo(String title, String description, int maxStudents, List<Image> photos, LessonDifficulty difficulty) {
		this(title, description, maxStudents, photos, difficulty, SubscriptionPack.STANDARD);
	}
}
