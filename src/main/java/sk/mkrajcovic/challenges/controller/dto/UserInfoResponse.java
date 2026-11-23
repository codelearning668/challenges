package sk.mkrajcovic.challenges.controller.dto;

import java.util.List;

public record UserInfoResponse (
		String username,
		String emailAddress,
		List<String> roles
) { }
