package fr.yoga.booking.domain.reservation;

import java.time.Instant;
import java.util.Comparator;

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
	private StudentRef student;
	private boolean approved;
	
	public Booking(Instant bookDate, User bookedBy, Student student, boolean approved) {
		this(bookDate, bookedBy, new StudentRef(student), approved);
	}

	public Booking(Instant bookDate, User bookedBy, UnregisteredUser student, boolean approved) {
		this(bookDate, bookedBy, new StudentRef(student), approved);
	}
	
	public boolean isForStudent(StudentRef student) {
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
	
	
	public static class SortByAscendingDateComparator implements Comparator<Booking> {
		@Override
		public int compare(Booking o1, Booking o2) {
			return o1.getBookDate().compareTo(o2.getBookDate());
		}
	}
}
