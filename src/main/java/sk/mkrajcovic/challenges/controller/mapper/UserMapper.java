package sk.mkrajcovic.challenges.controller.mapper;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.controller.dto.UserInfoResponse;
import sk.mkrajcovic.challenges.model.Authority;
import sk.mkrajcovic.challenges.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

	public static UserInfoResponse toDetailResponse(User user) {
		List<String> roles = user.getAuthorities().stream()
			.map(Authority::getRole)
			.toList();

		return new UserInfoResponse(user.getUsername(), roles);
	}

}
