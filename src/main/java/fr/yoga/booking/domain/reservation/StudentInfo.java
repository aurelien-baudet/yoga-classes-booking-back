package fr.yoga.booking.domain.reservation;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfo {
	private String id;
	private String displayName;
	private String email;
	private String phoneNumber;
	
	public StudentInfo(Student student) {
		this(student.getId(), student.getDisplayName(), null, null);
	}
	
	public StudentInfo(UnregisteredUser student) {
		this(null, student.getDisplayName(), student.getEmail(), student.getPhoneNumber());
	}
	
	public boolean isRegistered() {
		return id != null;
	}
	
	public boolean isSame(StudentInfo other) {
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
		return new UnregisteredUser(displayName, email, phoneNumber);
	}
}
