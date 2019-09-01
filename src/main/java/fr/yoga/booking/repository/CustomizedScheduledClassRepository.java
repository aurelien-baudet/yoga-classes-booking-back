package fr.yoga.booking.repository;

import java.util.List;

import fr.yoga.booking.domain.account.Student;
import fr.yoga.booking.domain.account.UnregisteredUser;
import fr.yoga.booking.domain.reservation.StudentInfo;
import fr.yoga.booking.domain.reservation.ScheduledClass;

public interface CustomizedScheduledClassRepository {
	List<ScheduledClass> findNextBookedClassesForStudent(StudentInfo student);
	List<ScheduledClass> findNextBookedClassesForStudent(Student student);
	List<ScheduledClass> findNextBookedClassesForStudent(UnregisteredUser student);

	boolean existsBookedClassForStudent(ScheduledClass bookedClass, StudentInfo student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, Student student);
	boolean existsBookedClassForStudent(ScheduledClass bookedClass, UnregisteredUser student);
}
