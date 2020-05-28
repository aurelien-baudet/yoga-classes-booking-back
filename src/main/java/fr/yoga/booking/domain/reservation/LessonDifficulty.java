package fr.yoga.booking.domain.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonDifficulty {
	private Integer sportLevel;
	private Integer postureLevel;
	
	public LessonDifficulty() {
		this(null, null);
	}
}
