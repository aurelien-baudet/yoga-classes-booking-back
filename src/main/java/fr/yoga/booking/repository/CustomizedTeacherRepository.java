package fr.yoga.booking.repository;

import java.util.List;

import fr.yoga.booking.domain.account.Teacher;

public interface CustomizedTeacherRepository {
	List<Teacher> findByEmailOrPhoneNumber(String emailOrPhoneNumber);
}
