package sk.mkrajcovic.challenges.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.security.UserRoles;

class UserMapperTest {

	@Test
	void shouldMapUserToDetailResponse() {
		var user = new User();
		user.setUsername("john.doe");
		user.setPassword("password");
		user.setEnabled(true);
		user.addAuthority(UserRoles.ADMIN);
		user.addAuthority(UserRoles.PARTICIPANT);

		var response = UserMapper.toDetailResponse(user);

		assertAll(
			() -> assertEquals("john.doe", response.username()),
			() -> assertEquals(List.of(UserRoles.ADMIN, UserRoles.PARTICIPANT), response.roles())
		);
	}

	@Test
	void shouldRejectNullUserWhenMappingToDetailResponse() {
		assertThrows(NullPointerException.class, () -> UserMapper.toDetailResponse(null));
	}
}
