package sk.mkrajcovic.challenges.controller.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {

	@NotNull
	private String message;
	private String type;

    @JsonInclude(Include.NON_NULL)
	private String code;

    @JsonInclude(Include.NON_NULL)
	private List<String> hints;

    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> naskResponse; 

    @JsonInclude(Include.NON_NULL)
    private String stackTrace;

	public ExceptionResponse() { /* intentionally empty */ }
}
