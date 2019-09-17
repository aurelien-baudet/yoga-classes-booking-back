package fr.yoga.booking.controller.dto;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ErrorDto {
	private final String code;
	private final String message;
	private final Instant timestamp;
	private final Map<String, Object> data;
	
	public ErrorDto(String code, Exception e) {
		this(code, e.getMessage(), Instant.now(), new HashMap<>());
	}
	
	public ErrorDto(String code, String message) {
		this(code, message, Instant.now(), new HashMap<>());
	}
	
	public ErrorDto addData(String key, Object value) {
		data.put(key, value);
		return this;
	}
}
