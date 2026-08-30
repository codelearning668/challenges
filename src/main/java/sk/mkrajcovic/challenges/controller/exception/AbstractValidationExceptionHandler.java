package sk.mkrajcovic.challenges.controller.exception;

import static java.util.stream.Collectors.joining;

import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import sk.mkrajcovic.challenges.controller.dto.ExceptionResponse;

public abstract class AbstractValidationExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handles validation errors raised by {@code @Valid @RequestBody}.
	 * <p>
	 * Field errors are returned in the form {@code field: message}, for example
	 * {@code newLapTime: must not be blank}.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		String message = buildFieldValidationMessage(ex.getBindingResult().getFieldErrors());

		var exceptionResponse = new ExceptionResponse();
		exceptionResponse.setMessage(message);
		exceptionResponse.setType(resolveErrorType());

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(exceptionResponse
		);
	}

	private String buildFieldValidationMessage(List<FieldError> fieldErrors) {
		return fieldErrors.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(joining(", "));
	}

	/**
	 * Handles validation errors raised by controller method validation.
	 *
	 * <p>
	 * For validated request objects, {@link ParameterErrors} provides the
	 * individual field errors. For direct method parameter constraints such as
	 * {@code @Positive @PathVariable}, the method parameter name is used.
	 */
	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(
			HandlerMethodValidationException ex,
			HttpHeaders headers,
			HttpStatusCode status,
			WebRequest request) {

		String message = buildParameterValidationMessage(ex.getAllValidationResults());

		var exceptionResponse = new ExceptionResponse();
		exceptionResponse.setMessage(message);
		exceptionResponse.setType(resolveErrorType());

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(exceptionResponse
		);
	}

	private String buildParameterValidationMessage(List<ParameterValidationResult> validationResults) {
		return validationResults.stream()
			.map(this::buildParameterValidationResultMessage)
			.filter(message -> !message.isBlank())
			.collect(joining(", "));
	}

	private String buildParameterValidationResultMessage(ParameterValidationResult validationResult) {
		if (validationResult instanceof ParameterErrors parameterErrors) {
			return buildFieldValidationMessage(parameterErrors.getFieldErrors());
		}
		return validationResult.getResolvableErrors().stream()
			.map(error -> buildResolvableErrorMessage(validationResult.getMethodParameter(), error))
			.collect(joining(", "));
	}

	private String buildResolvableErrorMessage(MethodParameter methodParameter, MessageSourceResolvable validationError) {
		return methodParameter.getParameterName()
			+ ": "
			+ (validationError.getDefaultMessage() != null
				? validationError.getDefaultMessage()
				: "Invalid value");
	}

	private String resolveErrorType() {
		return HttpStatus.Series.CLIENT_ERROR.name().replace("_", " ").toLowerCase();
	}

}