package sk.mkrajcovic.challenges.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger UI to use a static OpenAPI specification when runtime
 * OpenAPI generation is disabled with {@code springdoc.api-docs.enabled=false}.
 * <p>
 * The static specification will be served from
 * {@code src/main/resources/static/openapi}. This configuration deliberately
 * avoids generating OpenAPI documentation from Java annotations at runtime.
 * <p>
 * See: <a href=
 * "https://springdoc.org/#what-is-a-proper-way-to-set-up-swagger-ui-to-use-provided-spec-yml">
 * Springdoc documentation</a> for the static-specification setup.
 */
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", havingValue = "false", matchIfMissing = true)
@Configuration
public class StaticOpenApi {

	private static final Logger LOG = LoggerFactory.getLogger(StaticOpenApi.class);

	StaticOpenApi() {
		LOG.info("StaticOpenApi configuration loaded: Runtime OpenAPI generation is disabled.");
	}
}
