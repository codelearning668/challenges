package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.api.NotificationConfigurationApi;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationDetail;
import sk.mkrajcovic.challenges.controller.dto.NotificationConfigurationUpdateRequest;
import sk.mkrajcovic.challenges.controller.mapper.NotificationConfigurationMapper;
import sk.mkrajcovic.challenges.service.NotificationConfigurationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification-configuration")
class NotificationConfigurationController implements NotificationConfigurationApi {

	private final NotificationConfigurationService service;

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	public NotificationConfigurationDetail getNotificationConfiguration() {
		var notificationConfiguration = service.getNotificationConfiguration();
		return NotificationConfigurationMapper.toDetailResponse(notificationConfiguration);
	}

	@PutMapping(consumes = APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateNotificationConfiguration(@Valid @RequestBody NotificationConfigurationUpdateRequest request) {

		service.updateNotificationConfiguration(
			request.notifyOnChallengeCreated(),
			request.notifyOnChallengeClosed(),
			request.notifyOnChallengeLeaderChanged()
		);
	}

}
