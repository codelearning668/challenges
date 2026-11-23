package sk.mkrajcovic.challenges.context;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.util.Text;

/**
 * Provides access to the context of the current application call.
 * <p>
 * Encapsulates access to the current user and their roles, providing a stable
 * API for components that need information about the caller.
 * <p>
 * This class is request-scoped and is not intended to be instantiated directly.
 */
@Component @RequestScope
@RequiredArgsConstructor
@Getter
public class CallContext {

	private static final UserDetail NO_CURRENT_USER = new UserDetail(null, null, List.of());

	private final UserRepository userRepository;

	@Setter(AccessLevel.NONE)
	private UserDetail currentUser;

	/**
	 * Returns read-only details of the currently authenticated user.
	 * <p>
	 * The details are resolved lazily and cached for the remainder of the request.
	 * Roles reflect the effective security authorities, while the email address is
	 * read from the application user data.
	 *
	 * @return the authenticated user's details
	 */
	public UserDetail getCurrentUser() {
		if (currentUser == null) {
			currentUser = resolveCurrentUser();
		}
		return currentUser;
	}

	private UserDetail resolveCurrentUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return NO_CURRENT_USER;
		}
		String username = authentication.getName();

		return userRepository.findDetailByUsername(username)
			.map(user -> createUserDetail(user, authentication.getAuthorities()))
			.orElse(NO_CURRENT_USER);
	}

	private UserDetail createUserDetail(UserRepository.CurrentUser user, Collection<? extends GrantedAuthority> authorities) {
		return new UserDetail(user.getUsername(), user.getEmailAddress(), toRoles(authorities));
	}

	/**
	 * Checks whether the current user is in the specified role.
	 *
	 * @param role role to check; blank values are treated as not granted
	 * @return {@code true} if the current user is in the specified role;
	 *         {@code false} otherwise
	 */
	public boolean isUserInRole(String role) {
		var currentUser = getCurrentUser();
		return !Text.isBlank(role) && currentUser.roles().contains(role);
	}

	private List<String> toRoles(Collection<? extends GrantedAuthority> authorities) {
		if (authorities == null) {
			authorities = Collections.emptyList();
		}
		return authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();
	}

}
