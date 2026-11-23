package sk.mkrajcovic.challenges.controller.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.context.UserDetail;
import sk.mkrajcovic.challenges.controller.dto.UserInfoResponse;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

	public static UserInfoResponse toDetailResponse(UserDetail userDetail) {
		return new UserInfoResponse(
			userDetail.username(),
			userDetail.emailAddress(),
			userDetail.roles()
		);
	}

}
