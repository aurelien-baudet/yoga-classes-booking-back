package fr.yoga.booking.domain.reservation;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.yoga.booking.domain.account.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
	@Id
	private String id;
	private LessonInfo info;
	private Place place;
	private Teacher teacher;
	private boolean placeChanged;
	
	public Lesson(LessonInfo info, Place place, Teacher teacher) {
		this(null, info, place, teacher, false);
	}
}
