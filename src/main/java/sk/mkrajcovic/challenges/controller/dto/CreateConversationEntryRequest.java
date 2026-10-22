package sk.mkrajcovic.challenges.controller.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateConversationEntryRequest(

	@NotBlank @Length(max = 1000)
	@Schema(description = "Conversation message, up to 1000 characters.", maxLength = 1000, example = "Good luck everyone!")
	String message

) { }
