package fr.yoga.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.yoga.booking.domain.account.Teacher;

public interface TeacherRepository extends MongoRepository<Teacher, String> {
	boolean existsByAccountLogin(String login);
	Teacher findOneByAccountLogin(String login);
}
