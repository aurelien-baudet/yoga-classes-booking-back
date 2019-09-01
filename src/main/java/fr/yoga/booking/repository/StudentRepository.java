package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.account.Student;

public interface StudentRepository extends MongoRepository<Student, String> {
	Student findOneByAccountLogin(String login);
	boolean existsByAccountLogin(String login);
}
