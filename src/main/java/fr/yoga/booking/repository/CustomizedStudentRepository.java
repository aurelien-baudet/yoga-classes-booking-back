package fr.yoga.booking.repository;

import java.util.List;

import fr.yoga.booking.domain.account.Student;

public interface CustomizedStudentRepository {
	List<Student> findByEmailOrPhoneNumber(String emailOrPhoneNumber);
}
