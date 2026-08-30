package sk.mkrajcovic.challenges.controller.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import sk.mkrajcovic.challenges.config.MessageSource;
import sk.mkrajcovic.challenges.controller.dto.ExceptionResponse;
import sk.mkrajcovic.challenges.enums.MessageCodeConstants;
import sk.mkrajcovic.challenges.exception.AccessDenied;
import sk.mkrajcovic.challenges.exception.BusinessViolation;
import sk.mkrajcovic.challenges.exception.ClientException;
import sk.mkrajcovic.challenges.exception.Conflict;
import sk.mkrajcovic.challenges.exception.GlobalException;
import sk.mkrajcovic.challenges.exception.InfrastructureException;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;

@ControllerAdvice
public class ExceptionHandlerController extends AbstractValidationExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerController.class);

	/**
	 * Maps a specific exception type to its HTTP status.
	 * <p>
	 * Lookup walks up the class hierarchy, so unlisted subclasses of
	 * {@link ClientException} inherit the 400 default and subclasses of
	 * {@link InfrastructureException} inherit the 500 default.
	 * <p>
	 * Add a new entry to register an exception:
	 * {@code Conflict.class -> HttpStatus.CONFLICT}
	 */
	private static final Map<Class<? extends Exception>, HttpStatus> STATUS_MAP = Map.of(
		// main parents
		ClientException.class, HttpStatus.BAD_REQUEST,
		InfrastructureException.class, HttpStatus.INTERNAL_SERVER_ERROR,
		// children
		BusinessViolation.class, HttpStatus.UNPROCESSABLE_ENTITY,
		ResourceNotFound.class, HttpStatus.NOT_FOUND,
		Conflict.class, HttpStatus.CONFLICT,
		AccessDenied.class, HttpStatus.FORBIDDEN
	);

	private final MessageSource messageSource;
	private final boolean displayStackTrace;

	public ExceptionHandlerController(MessageSource messageSource,
			@Value("${challenges.exception.display-stack-trace:false}") boolean displayStackTrace) {

		this.messageSource = messageSource;
		this.displayStackTrace = displayStackTrace;
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex) {
		LOG.error(MessageCodeConstants.UNEXPECTED_ERROR, ex);
		var exceptionDto = new ExceptionResponse();
		exceptionDto.setMessage("Access denied");
		exceptionDto.setType(resolveErrorType(HttpStatus.Series.CLIENT_ERROR));
		exceptionDto.setStackTrace(displayStackTrace ? readStackTrace(ex) : null);
		return new ResponseEntity<>(exceptionDto, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
		LOG.error(MessageCodeConstants.UNEXPECTED_ERROR, ex);
		return createResponseEntity(ex).body(serializeException(ex));
	}

	private BodyBuilder createResponseEntity(Exception exception) {
		HttpStatus status = resolveHttpStatus(exception);
		return ResponseEntity.status(status);
	}

	private ExceptionResponse serializeException(Exception exception) {
		var exceptionDto = new ExceptionResponse();
		exceptionDto.setStackTrace(displayStackTrace ? readStackTrace(exception) : null);

		if (exception instanceof GlobalException global) {
			exceptionDto.setMessage(messageSource.getMessage(global.getCode(), global.getArgs()));
			exceptionDto.setType(resolveErrorType(resolveHttpStatus(exception).series()));
			exceptionDto.setCode(global.getCode());
		} else {
			exceptionDto.setMessage(messageSource.getMessage(MessageCodeConstants.UNEXPECTED_ERROR));
			exceptionDto.setType(resolveErrorType(HttpStatus.Series.SERVER_ERROR));
		}
		return exceptionDto;
	}

	/**
	 * Resolves the HTTP status for a given exception.
	 * <p>
	 * Walks up the class hierarchy starting from the concrete exception type. The
	 * first ancestor present in {@link #STATUS_MAP} wins. If nothing matches, falls
	 * back to 500.
	 * <p>
	 * This lets you register a single entry for a parent exception (e.g.
	 * {@code ClientException.class -> BAD_REQUEST}) and all its children inherit it
	 * unless overridden by a more specific mapping.
	 * <p>
	 * To add a new mapping, add a line to {@link #STATUS_MAP}.
	 */
	private HttpStatus resolveHttpStatus(Exception exception) {
		Class<?> current = exception.getClass();
		while (current != null && Exception.class.isAssignableFrom(current)) {
			HttpStatus mapped = STATUS_MAP.get(current);
			if (mapped != null) {
				return mapped;
			}
			current = current.getSuperclass();
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private String resolveErrorType(HttpStatus.Series series) {
		return series.name().replace("_", " ").toLowerCase();
	}

	private String readStackTrace(Throwable throwable) {
		StringBuilder result = new StringBuilder();
		StackTraceElement[] stackTrace = throwable.getStackTrace();

		result.append(this).append("\n");
		for (int x = 0; x < stackTrace.length; x++) {
			result.append("\tat ").append(stackTrace[x].toString());
			if (x != stackTrace.length - 1) {
				result.append("\n");
			}
		}
		return result.toString();
	}
}