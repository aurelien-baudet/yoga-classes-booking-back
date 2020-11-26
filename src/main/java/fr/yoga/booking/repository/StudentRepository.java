package fr.yoga.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.account.Student;

public interface StudentRepository extends MongoRepository<Student, String>, CustomizedStudentRepository {
	Student findOneByAccountLogin(String login);
	boolean existsByAccountLogin(String login);
	Student findOneByContactEmail(String username);
	Page<Student> findAllByDisplayNameContaining(String name, Pageable page);
}
