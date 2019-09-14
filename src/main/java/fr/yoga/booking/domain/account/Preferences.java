package fr.yoga.booking.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Preferences {
	private boolean visibleByOtherStudents;
	private boolean agreesToBeAssisted;
	private boolean addBookedClassesToCalendar;
}
