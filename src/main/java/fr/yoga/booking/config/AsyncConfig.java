package fr.yoga.booking.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@ConditionalOnProperty(name="async.enabled", matchIfMissing=true)
public class AsyncConfig {

}
