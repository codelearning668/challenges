package sk.mkrajcovic.challenges.context;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

	@Setter(AccessLevel.NONE)
	private String currentUser;

	/**
	 * Returns the name of the currently authenticated user.
	 * <p>
	 * The user name is resolved lazily from the current security context on the
	 * first invocation and cached for the remainder of the request.
	 *
	 * @return the current user's name, or {@code null} if no user is authenticated
	 */
	public String getCurrentUser() {
		if (currentUser == null) {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				currentUser = authentication.getName();
			}
		}
		return currentUser;
	}

	/**
	 * Checks whether the current user is in the specified role.
	 *
	 * @param role role to check; blank values are treated as not granted
	 * @return {@code true} if the current user is in the specified role;
	 *         {@code false} otherwise
	 */
	public boolean isUserInRole(String role) {
		if (Text.isBlank(role)) {
			return false;
		}
		for (var simpleGrantedAuthority : getUserAuthorities()) {
			if (simpleGrantedAuthority.getAuthority().equals(role)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Collection<SimpleGrantedAuthority> getUserAuthorities() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null
			? (Collection<SimpleGrantedAuthority>) authentication.getAuthorities()
			: Collections.emptyList();
	}

}
