package sk.mkrajcovic.challenges.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuration class that enables method-level authorization using JSR-250
 * security annotations.
 * <p>
 * This configuration is conditionally loaded based on the
 * <code>permit-all</code> authentication switch.<br>
 * If authentication is disabled (i.e., <code>permit-all</code> is set to
 * <code>true</code>), method-level authorization will also be disabled.
 * <p>
 * When active, this configuration ensures that role-based method-level
 * authorization is enforced throughout the application.
 *
 * @author mkrajcovicux
 */
@Configuration
@EnableMethodSecurity(jsr250Enabled = true)
public class MethodLevelSecurityConfig {

	private static final Logger LOG = LoggerFactory.getLogger(MethodLevelSecurityConfig.class);

	MethodLevelSecurityConfig() {
		LOG.info("(JSR-250) Role-based method level authorization is enabled");
	}
}
