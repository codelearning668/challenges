package sk.mkrajcovic.challenges.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.mkrajcovic.challenges.enums.MessageCodeConstants;
import sk.mkrajcovic.challenges.exception.Conflict;
import sk.mkrajcovic.challenges.model.User;
import sk.mkrajcovic.challenges.repository.persistence.UserRepository;
import sk.mkrajcovic.challenges.security.UserRoles;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * Registers a new user with the specified username and password.
	 * <p>
	 * The password is encoded before it is persisted.<br>
	 * The new user is enabled and is assigned to PARTICIPANT role.
	 *
	 * @param username name for the new user
	 * @param rawPassword unencoded password for the new user
	 * @throws Conflict if the specified username is already registered
	 */
	@Transactional
	public void registerNewUser(String username, String rawPassword) {
		verifyUsernameIsAvailable(username);

		var newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(passwordEncoder.encode(rawPassword));
		newUser.setEnabled(true);
		newUser.addAuthority(UserRoles.PARTICIPANT);

		userRepository.save(newUser);
		/*
		 * Registration payload is excluded from request logging by
		 * default because it carries credentials;
		 * This line is the only record of who was actually created.
		 */
		LOG.info("Registered new user '{}'", username);
	}

	private void verifyUsernameIsAvailable(String username) {
		if (userRepository.existsById(username)) {
			throw new Conflict(MessageCodeConstants.USERNAME_ALREADY_TAKEN);
		}
	}
}
