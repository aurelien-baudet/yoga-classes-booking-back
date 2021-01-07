package fr.yoga.booking.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import fr.yoga.booking.service.technical.error.MultiUncaughtExceptionHandler;
import fr.yoga.booking.service.technical.error.RepositoryUncaughtExceptionHandler;
import fr.yoga.booking.service.technical.error.UnmanagedErrorRepository;

@Configuration
@EnableAsync
@ConditionalOnProperty(name="async.enabled", matchIfMissing=true)
public class AsyncConfig implements AsyncConfigurer {
	@Autowired UnmanagedErrorRepository repository;

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new MultiUncaughtExceptionHandler(new SimpleAsyncUncaughtExceptionHandler(), new RepositoryUncaughtExceptionHandler(repository));
	}
	
}
