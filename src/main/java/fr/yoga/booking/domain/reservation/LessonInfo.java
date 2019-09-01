package fr.yoga.booking.domain.reservation;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonInfo {
	private String title;
	private String description;
	private int maxStudents;
	private List<Photo> photos;
	
	public LessonInfo() {
		this(null, null, 0, new ArrayList<>());
	}
}
