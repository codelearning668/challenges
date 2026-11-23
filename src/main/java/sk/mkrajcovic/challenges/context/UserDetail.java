package sk.mkrajcovic.challenges.context;

import java.util.List;

/**
 * Read-only details of the user authenticated for the current request.
 * <p>
 * Roles reflect the effective authorities from Spring Security, while the email
 * address comes from the application's user data.
 */
public record UserDetail(
		String username,
		String emailAddress,
		List<String> roles) {

	public UserDetail {
		roles = List.copyOf(roles);
	}
}
