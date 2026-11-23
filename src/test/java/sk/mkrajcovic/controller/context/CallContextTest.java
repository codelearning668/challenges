package sk.mkrajcovic.controller.context;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sk.mkrajcovic.challenges.security.UserRoles.ADMIN;
import static sk.mkrajcovic.challenges.security.UserRoles.PARTICIPANT;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import sk.mkrajcovic.challenges.context.CallContext;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;

class CallContextTest {

	private static final String USERNAME = "user";
	private static final String PASSWORD = "password";
	private static final String EMAIL_ADDRESS = "user@example.com";

	private UserRepository userRepository;
	private CallContext callContext;

	@BeforeEach
	void setUp() {
		userRepository = mock(UserRepository.class);
		callContext = new CallContext(userRepository);
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldReturnEmptyUserDetailWhenThereIsNoAuthentication() {
		var userDetail = callContext.getCurrentUser();

		assertAll(
			() -> assertEquals(null, userDetail.username()),
			() -> assertEquals(null, userDetail.emailAddress()),
			() -> assertEquals(List.of(), userDetail.roles())
		);
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
	void shouldReturnFalseWhenAuthenticatedUserDoesNotExist() {
		setAuthentication(PARTICIPANT);
		when(userRepository.findDetailByUsername(USERNAME)).thenReturn(Optional.empty());

		assertFalse(callContext.isUserInRole(PARTICIPANT));
	}

	@Test
	void shouldReturnEmptyUserDetailWhenAuthenticatedUserDoesNotExist() {
		setAuthentication(PARTICIPANT);
		when(userRepository.findDetailByUsername(USERNAME)).thenReturn(Optional.empty());

		var userDetail = callContext.getCurrentUser();

		assertAll(
			() -> assertEquals(null, userDetail.username()),
			() -> assertEquals(null, userDetail.emailAddress()),
			() -> assertEquals(List.of(), userDetail.roles())
		);
	}

	@Test
	void shouldReturnAuthenticatedUserDetail() {
		setAuthentication(PARTICIPANT, ADMIN);
		stubUserDetail();

		var userDetail = callContext.getCurrentUser();

		assertAll(
			() -> assertEquals(USERNAME, userDetail.username()),
			() -> assertEquals(EMAIL_ADDRESS, userDetail.emailAddress()),
			() -> assertEquals(List.of(PARTICIPANT, ADMIN), userDetail.roles())
		);
	}

	@Test
	void shouldCacheAuthenticatedUserDetail() {
		setAuthentication(PARTICIPANT);
		stubUserDetail();

		var firstUserDetail = callContext.getCurrentUser();
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("other-user", PASSWORD, List.of(new SimpleGrantedAuthority(ADMIN)))
		);
		var cachedUserDetail = callContext.getCurrentUser();

		assertSame(firstUserDetail, cachedUserDetail);
		verify(userRepository).findDetailByUsername(USERNAME);
	}

	@Test
	void shouldReturnTrueWhenUserHasRole() {
		setAuthentication(PARTICIPANT, ADMIN);
		stubUserDetail();

		assertTrue(callContext.isUserInRole(PARTICIPANT));
		assertTrue(callContext.isUserInRole(ADMIN));
	}

	@Test
	void shouldReturnFalseWhenUserDoesNotHaveRole() {
		setAuthentication(PARTICIPANT);
		stubUserDetail();

		assertFalse(callContext.isUserInRole(ADMIN));
	}

	@Test
	void shouldRequireExactRoleMatch() {
		setAuthentication(PARTICIPANT);
		stubUserDetail();

		assertTrue(callContext.isUserInRole(PARTICIPANT));
		assertFalse(callContext.isUserInRole("ROLE_PARTICIPANT"));
		assertFalse(callContext.isUserInRole("participant"));
	}

	private void setAuthentication(String... roles) {
		var authorities = List.of(roles).stream()
			.map(SimpleGrantedAuthority::new)
			.toList();
		var authentication = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private void stubUserDetail() {
		UserRepository.CurrentUser userDetail = new UserRepository.CurrentUser() {
			@Override
			public String getUsername() {
				return USERNAME;
			}

			@Override
			public String getEmailAddress() {
				return EMAIL_ADDRESS;
			}
		};
		when(userRepository.findDetailByUsername(USERNAME)).thenReturn(Optional.of(userDetail));
	}
}
