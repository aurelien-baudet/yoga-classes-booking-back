package fr.yoga.booking.domain.reservation;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRef {
	private String id;
	private String displayName;
	private boolean registered;
	
	public StudentRef(Student student) {
		this(student.getId(), student.getDisplayName(), true);
	}
	
	public StudentRef(UnregisteredUser student) {
		this(student.getId(), student.getDisplayName(), false);
	}
	
	public boolean isRegistered() {
		return registered;
	}
	
	public boolean isSame(StudentRef other) {
		if(isRegistered() && other.isRegistered()) {
			return toStudent().isSame(other.toStudent());
		}
		if(!isRegistered() && !other.isRegistered()) {
			return toUnregisteredUser().isSame(other.toUnregisteredUser());
		}
		return false;
	}
	
	public boolean isSame(Student student) {
		if(isRegistered()) {
			return toStudent().isSame(student);
		}
		return false;
	}

	public boolean isSame(UnregisteredUser student) {
		if(!isRegistered()) {
			return toUnregisteredUser().isSame(student);
		}
		return false;
	}

	public Student toStudent() {
		return new Student(id);
	}
	
	public UnregisteredUser toUnregisteredUser() {
		return new UnregisteredUser(id);
	}
}
