package sk.mkrajcovic.challenges.repository.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.mkrajcovic.challenges.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	@Query("""
		SELECT u.username AS username,
		       u.emailAddress AS emailAddress
		FROM User u
		WHERE u.username = :username
	""")
	Optional<CurrentUser> findDetailByUsername(@Param("username") String username);

	interface CurrentUser {
		String getUsername();
		String getEmailAddress();
	}
}
