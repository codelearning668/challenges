package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.context.UserDetail;
import sk.mkrajcovic.challenges.security.UserRoles;

class UserMapperTest {

	@Test
	void shouldMapUserDetailToDetailResponse() {
		var userDetail = new UserDetail(
			"john.doe",
			"john.doe@example.com",
			List.of(UserRoles.ADMIN, UserRoles.PARTICIPANT)
		);

		var response = UserMapper.toDetailResponse(userDetail);

		assertAll(
			() -> assertEquals("john.doe", response.username()),
			() -> assertEquals("john.doe@example.com", response.emailAddress()),
			() -> assertEquals(List.of(UserRoles.ADMIN, UserRoles.PARTICIPANT), response.roles())
		);
	}

	@Test
	void shouldRejectNullUserDetailWhenMappingToDetailResponse() {
		assertThrows(NullPointerException.class, () -> UserMapper.toDetailResponse(null));
	}
}
