package sk.mkrajcovic.controller.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import sk.mkrajcovic.challenges.context.CallContext;
import static sk.mkrajcovic.challenges.security.UserRoles.PARTICIPANT;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;

class CallContextTest {

	private static final String USERNAME = "user";
	private static final String PASSWORD = "password";

	private CallContext callContext;

	@BeforeEach
	void setUp() {
		callContext = new CallContext();
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldReturnNullWhenThereIsNoAuthentication() {
		assertNull(callContext.getCurrentUser());
	}

	@Test
	void shouldReturnCurrentUserFromAuthentication() {
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD,
				List.of(new SimpleGrantedAuthority(PARTICIPANT)));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertEquals(USERNAME, callContext.getCurrentUser());
	}

	@Test
	void shouldCacheCurrentUser() {
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, List.of());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertEquals(USERNAME, callContext.getCurrentUser());

		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, List.of()));

		// the value is cached in CallContext
		assertEquals(USERNAME, callContext.getCurrentUser());
	}

	@Test
	void shouldReturnFalseWhenRoleIsNull() {
		assertFalse(callContext.isUserInRole(null));
	}

	@Test
	void shouldReturnFalseWhenRoleIsBlank() {
		assertFalse(callContext.isUserInRole(""));
		assertFalse(callContext.isUserInRole(" "));
	}

	@Test
	void shouldReturnFalseWhenThereIsNoAuthentication() {
		assertFalse(callContext.isUserInRole(PARTICIPANT));
	}

	@Test
	void shouldReturnTrueWhenUserHasRole() {
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, "password",
				List.of(new SimpleGrantedAuthority(PARTICIPANT), new SimpleGrantedAuthority(ADMIN)));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertTrue(callContext.isUserInRole(PARTICIPANT));
		assertTrue(callContext.isUserInRole(ADMIN));
	}

	@Test
	void shouldReturnFalseWhenUserDoesNotHaveRole() {
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD,
				List.of(new SimpleGrantedAuthority(PARTICIPANT)));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertFalse(callContext.isUserInRole(ADMIN));
	}

	@Test
	void shouldRequireExactRoleMatch() {
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD,
				List.of(new SimpleGrantedAuthority(PARTICIPANT)));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertTrue(callContext.isUserInRole(PARTICIPANT));
		assertFalse(callContext.isUserInRole("ROLE_PARTICIPANT"));
		assertFalse(callContext.isUserInRole("participant"));
	}
}
