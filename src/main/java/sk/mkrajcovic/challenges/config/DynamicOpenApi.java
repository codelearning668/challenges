package sk.mkrajcovic.challenges.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Enables runtime generation of the OpenAPI document when
 * {@code springdoc.api-docs.enabled=true}.
 * <p>
 * Swagger UI can load a versioned static OpenAPI specification from
 * {@code src/main/resources/static/openapi} independently of this
 * configuration. Enable runtime generation only when regenerating that
 * specification or otherwise inspecting the generated contract.
 * <p>
 * Defines the application metadata, deployment-relative server URL, and HTTP
 * Basic authentication scheme used by secured operations.
 */
@OpenAPIDefinition(
	info = @Info(
		title = "Challenges API",
		version = "1.0.0",
		description = "REST API for organising and recording racing-simulator challenges. Participants register for car-and-track challenges, submit lap times, and compare results on a live leaderboard."
	),
	servers = @Server(url = "/challenges/svc")
)
@SecurityScheme(
	name = "basicAuth",
	type = SecuritySchemeType.HTTP,
	scheme = "basic"
)
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "true", matchIfMissing = false)
@Configuration
public class DynamicOpenApi {

	private static final Logger LOG = LoggerFactory.getLogger(DynamicOpenApi.class);

	DynamicOpenApi() {
		LOG.info("DynamicOpenApi configuration loaded: Runtime OpenAPI generation is enabled. "
			+ "Run 'mvn generate-sources -Papi-docs' to update static documentation. "
			+ "Use -Dapp.port=xxxx at the end to override the default port (8790) if needed.");
	}
}
