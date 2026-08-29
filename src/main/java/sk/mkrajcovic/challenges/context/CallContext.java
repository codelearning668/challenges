package sk.mkrajcovic.challenges.context;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

@Component @RequestScope
@RequiredArgsConstructor
@Getter
public class CallContext {

	@Setter(AccessLevel.NONE)
	private String currentUser;

	private final LocalDateTime transactionTime;

	public CallContext() {
		this.transactionTime = LocalDateTime.now(ZoneOffset.UTC);
	}

	public String getCurrentUser() {
		if (currentUser == null) {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				currentUser = authentication.getName();
			}
		}
		return currentUser;
	}

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
