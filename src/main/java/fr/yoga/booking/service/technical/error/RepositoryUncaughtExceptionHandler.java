package fr.yoga.booking.service.technical.error;

import java.lang.reflect.Method;
import java.util.StringJoiner;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RepositoryUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
	private final UnmanagedErrorRepository repository;
	
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		StringJoiner joiner = new StringJoiner(", ", "(", ")");
		for (Object param : params) {
			joiner.add(param==null ? "null" : param.toString());
		}
		repository.save(new UnmanagedError(method.getName()+joiner.toString(), ex));
	}


}
