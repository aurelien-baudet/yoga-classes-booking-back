package fr.yoga.booking.service.technical.error;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnmanagedError {
	@Id
	private String id;
	private String context;
	private String message;
	private String type;
	private String stackTrace;
	private UnmanagedError cause;
	
	public UnmanagedError(String context, Throwable e) {
		this(context, e.getMessage(), e.getClass().getTypeName(), ExceptionUtils.getStackTrace(e), e.getCause() == null ? null : new UnmanagedError(context, e.getCause()));
	}

	public UnmanagedError(String context, String message, String type, String stackTrace, UnmanagedError cause) {
		this(null, context, message, type, stackTrace, cause);
	}
	
}
