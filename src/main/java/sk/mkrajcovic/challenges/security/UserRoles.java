package sk.mkrajcovic.challenges.security;

/**
 * Application roles used for authorization.
 * <p>
 * Roles are cumulative, meaning a user may be assigned multiple roles and
 * therefore have access to the functionality associated with each assigned
 * role.
 */
public class UserRoles {

	/** Registered users with usually participating in challenges */
	public static final String PARTICIPANT = "PARTICIPANT";

	/** Administration of cars, tracks and more yet undefined */
	public static final String ADMIN = "ADMIN";

}
