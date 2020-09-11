package fr.yoga.booking.service.technical.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class SimpleRandomGenerator implements RandomGenerator {

	@Override
	public String generate(int count) {
		return RandomStringUtils.randomAlphanumeric(count);
	}

}
