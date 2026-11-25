package sk.mkrajcovic.challenges.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configures the bounded executor used for non-blocking notification delivery.
 * <p>
 * Notification email performs remote I/O and must not occupy the HTTP request
 * thread that created the challenge. A dedicated bounded pool also avoids using
 * the JVM-wide common pool and limits the resources a burst of notifications
 * can consume.
 */
@Configuration
@EnableAsync
public class NotificationAsyncConfig {

	/**
	 * Executor selected by {@code @Async("notificationExecutor")} notification
	 * listeners.
	 *
	 * @return bounded executor for background notification work
	 */
	@Bean("notificationExecutor")
	Executor notificationExecutor() {
		var executor = new ThreadPoolTaskExecutor();
		/*
		 * Send one notification batch at a time and retain at most ten waiting
		 * batches. This preserves submission order and bounds notification work.
		 *
		 * When the provider is slow and the queue is full, Spring rejects a new
		 * asynchronous task after challenge creation has already committed. That
		 * email is intentionally skipped because this best-effort feature has no
		 * delivery persistence or retry mechanism.
		 */
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(1);
		executor.setQueueCapacity(10);
		executor.setThreadNamePrefix("notification-");
		executor.initialize();

		return executor;
	}
}
