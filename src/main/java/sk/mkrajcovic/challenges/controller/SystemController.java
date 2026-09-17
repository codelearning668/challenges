package sk.mkrajcovic.challenges.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.UserInfoResponse;
import sk.mkrajcovic.challenges.controller.mapper.UserMapper;
import sk.mkrajcovic.challenges.service.SystemService;

@RestController
@RequiredArgsConstructor
public class SystemController {

	private final SystemService systemService;

	@GetMapping(path = "/users/info", produces = APPLICATION_JSON_VALUE)
	public UserInfoResponse getUserInfo() {
		var user = systemService.getUserInfo();
		return UserMapper.toDetailResponse(user);
	}

}
