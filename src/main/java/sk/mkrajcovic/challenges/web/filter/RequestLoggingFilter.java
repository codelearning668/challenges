package sk.mkrajcovic.challenges.web.filter;

import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ConditionalOnProperty(name = "challenges.request-logging.enabled", havingValue = "true", matchIfMissing = false)
@Component
@Order(Ordered.HIGHEST_PRECEDENCE - 10)
class RequestLoggingFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);
	private static final List<HttpMethod> SUPPORTED_HTTP_METHODS = List.of(POST, PUT, PATCH);

	private final ObjectMapper jsonMapper;
	private final List<String> excludedPaths;
	private final AntPathMatcher pathMatcher;

	RequestLoggingFilter(@Value("${challenges.request-logging.exclude-paths:#{null}}") List<String> pathsToExclude) {
		LOG.info("Initializing request logging for HTTP methods {}", SUPPORTED_HTTP_METHODS);
		jsonMapper = new ObjectMapper();
		excludedPaths = initExcludedPaths(pathsToExclude);
		pathMatcher = new AntPathMatcher();
	}

	private List<String> initExcludedPaths(List<String> pathsToExclude) {
		if (pathsToExclude == null || pathsToExclude.isEmpty()) {
			LOG.warn("No exclude-paths configured. All incoming JSON payloads will be logged.");
			return List.of();
		}
		List<String> excluded = List.copyOf(pathsToExclude);
		LOG.info("Excluding logging for the following paths: {}", excluded);
		return excluded;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (shouldLog(request)) {
			var requestWrapper = new BufferedRequestWrapper(request);
			logIncomingRequest(requestWrapper);
			request = requestWrapper;
		}
		filterChain.doFilter(request, response);
	}

	protected boolean shouldLog(HttpServletRequest request) {
		String requestUri = request.getRequestURI()
			.substring(request.getContextPath().length());

		return SUPPORTED_HTTP_METHODS.contains(HttpMethod.valueOf(request.getMethod()))
			&& MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())
			&& isNotExlcudedFromLogging(requestUri);
	}

	private boolean isNotExlcudedFromLogging(String requestUri) {
		return  excludedPaths.stream()
			.noneMatch(antPath -> pathMatcher.match(antPath, requestUri));
	}

	private void logIncomingRequest(BufferedRequestWrapper request) {
		StringBuilder message = new StringBuilder(250)
			.append(request.getMethod()).append(": ")
			.append(request.getRequestURI());

		if (!StringUtils.isBlank(request.getQueryString())) {
			message.append('?').append(request.getQueryString());
		}
		message.append(", ").append(compactJsonSafely(request.getRequestBody()));
		logger.info(message);
	}

    private String compactJsonSafely(String input) {
		if (StringUtils.isBlank(input)) {
			return "<no payload>";
		}
		try {
			Object json = jsonMapper.readValue(input, Object.class);
			return jsonMapper.writeValueAsString(json);
		} catch (Exception e) {
			return "<non-json payload>";
		}
	}
}
