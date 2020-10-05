package fr.yoga.booking.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import fr.yoga.booking.domain.account.Teacher;
import fr.yoga.booking.util.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomizedTeacherRepositoryImpl implements CustomizedTeacherRepository {
	private final MongoOperations mongo;

	@Override
	public List<Teacher> findByEmailOrPhoneNumber(String emailOrPhoneNumber) {
		Criteria where = new Criteria().orOperator(where("contact.email").is(emailOrPhoneNumber), 
				where("contact.phoneNumber").is(emailOrPhoneNumber), 
				where("contact.phoneNumber").regex(PhoneNumberUtil.toSearchExpression(emailOrPhoneNumber)));
		return mongo.find(query(where), Teacher.class);
	}


}
