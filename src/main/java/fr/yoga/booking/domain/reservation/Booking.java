package fr.yoga.booking.domain.reservation;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.account.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Booking {
	@NotNull
	private Instant bookDate;
//	@NotNull
	private User bookedBy;
	@NotNull
	private StudentInfo student;
	
	public Booking(Instant bookDate, User bookedBy, Student student) {
		this(bookDate, bookedBy, new StudentInfo(student));
	}

	public Booking(Instant bookDate, User bookedBy, UnregisteredUser student) {
		this(bookDate, bookedBy, new StudentInfo(student));
	}
	
	public boolean isForStudent(StudentInfo student) {
		return this.student.isSame(student);
	}
	
	public boolean isForStudent(Student student) {
		return this.student.isSame(student);
	}
	
	public boolean isForStudent(UnregisteredUser student) {
		return this.student.isSame(student);
	}
	
	public boolean isSame(Booking other) {
		return other.getBookDate().equals(bookDate)
				&& isSameUser(other.getBookedBy())
				&& student.isSame(other.getStudent());
	}
	
	private boolean isSameUser(User bookedBy) {
		if(this.bookedBy == null) {
			return bookedBy == null;
		}
		return this.bookedBy.isSame(bookedBy);
	}
}
