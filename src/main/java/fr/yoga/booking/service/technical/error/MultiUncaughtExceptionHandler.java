package fr.yoga.booking.service.technical.error;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {
	private final List<AsyncUncaughtExceptionHandler> delegates;
	
	public MultiUncaughtExceptionHandler(AsyncUncaughtExceptionHandler... delegates) {
		this(Arrays.asList(delegates));
	}
	
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		for (AsyncUncaughtExceptionHandler delegate : delegates) {
			delegate.handleUncaughtException(ex, method, params);
		}
	}

}
