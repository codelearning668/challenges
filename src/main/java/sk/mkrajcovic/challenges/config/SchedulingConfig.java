package sk.mkrajcovic.challenges.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's processing of {@code @Scheduled} methods.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

}
