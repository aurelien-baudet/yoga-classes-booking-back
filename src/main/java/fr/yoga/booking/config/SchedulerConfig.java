package fr.yoga.booking.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {
	@Configuration
	@EnableScheduling
	@ConditionalOnProperty(name="scheduling.enabled", matchIfMissing=true)
	public static class EnableSpringScheduling {
		
	}
	
	@Bean
	public TaskScheduler scheduler() {
		return new ThreadPoolTaskScheduler();
	}
}
