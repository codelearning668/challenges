package sk.mkrajcovic.challenges.service;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import sk.mkrajcovic.challenges.model.Authority;
import sk.mkrajcovic.challenges.model.User;

@Service
public class SystemService {

	public User getUserInfo() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		var user = new User();
		user.setUsername(authentication.getName());
		user.setAuthorities(toDomainAuthorities(authentication.getAuthorities()));

		return user;
	}

	private List<Authority> toDomainAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
		if (grantedAuthorities == null) {
			return emptyList();
		}
		return grantedAuthorities.stream()
			.map(GrantedAuthority::getAuthority)
			// this is not good, that's why we should have real domain objects
			// + shouldn't we be able to read the whole info from the CallContext??
			.map(authority -> new Authority(null, authority))
			.toList();
	}

}
