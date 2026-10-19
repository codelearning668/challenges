package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import sk.mkrajcovic.challenges.controller.api.ConversationApi;
import sk.mkrajcovic.challenges.service.ConversationService;

@Controller
@AllArgsConstructor
public class ConversationController implements ConversationApi {

	private final ConversationService conversationService;

	@RolesAllowed(ADMIN)
	@DeleteMapping(path = "/conversation-entries/{entryId}")
	@ResponseStatus(NO_CONTENT)
	public void deleteConversationEntry(@PathVariable @Positive Integer entryId) {
		conversationService.deleteConversationEntry(entryId);
	}

}
