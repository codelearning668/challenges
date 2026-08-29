package sk.mkrajcovic.challenges.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO used for API error responses.
 * <p>
 * This DTO intentionally remains a mutable class rather than a record.
 * Error responses may contain optional fields depending on the exception,
 * and the exception handler builds the response incrementally.<br>
 * Using a record would introduce unnecessary constructor parameters and
 * reduce readability at the call site.
 */
@Getter @Setter
public class ExceptionResponse {

	@NotNull
	private String message;
	private String type;

	@JsonInclude(Include.NON_NULL)
	private String code;

	@JsonInclude(Include.NON_NULL)
	private String stackTrace;

	public ExceptionResponse() { /* intentionally empty */ }
}
