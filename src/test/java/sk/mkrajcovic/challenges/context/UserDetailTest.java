package sk.mkrajcovic.challenges.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class UserDetailTest {

	@Test
	void copiesRolesToPreventLaterCallerMutation() {
		var roles = new ArrayList<>(List.of("PARTICIPANT"));

		var userDetail = new UserDetail("racer1", "racer1@example.com", roles);
		roles.add("ADMIN");

		assertEquals(List.of("PARTICIPANT"), userDetail.roles());
	}

	@Test
	void exposesUnmodifiableRoles() {
		var userDetail = new UserDetail("racer1", "racer1@example.com", List.of("PARTICIPANT"));

		assertThrows(UnsupportedOperationException.class, () -> userDetail.roles().add("ADMIN"));
	}
}
