package sk.mkrajcovic.challenges.repository.util;

import org.springframework.data.repository.CrudRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.enums.MessageCodeConstants;
import sk.mkrajcovic.challenges.exception.Conflict;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;
import sk.mkrajcovic.challenges.model.BaseEntity;

@NoArgsConstructor(access = AccessLevel.NONE)
public class EntityUtils {

	/**
	 * Use this method as pre-check before hitting the database, as it may be
	 * beneficial by reducing useless database access and providing better user
	 * friendly message about the issue.
	 *
	 * @param entity  BaseEntity instance
	 * @param version value to check against
	 * @throws IllegalArgumentException if entity argument is {@code null}
	 * @throws Conflict if the business version of an entity does  not match the second argument
	 */

	public static void checkStaleUpdate(BaseEntity entity, int version) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null to perform this check");
		}
		if (entity.getVersion() != version) {
			throw new Conflict(MessageCodeConstants.STALE_UPDATE);
		}
	}

	/**
	 * Retrieves an entity from the given {@link CrudRepository} if it is found,
	 * otherwise throws client error initialized with
	 * {@link MessageCodeConstants#RESOURCE_NOT_FOUND}.
	 *
	 * @param repository from which to retrieve the entity
	 * @param id primary identifier of the entity
	 * @return a valid entity object from the given repository
	 * @throws ResourceNotFound if the entity is not found in repository
	 * @throws IllegalArgumentException if either argument is {@code null}
	 */
	public static <T> T getExistingEntityById(CrudRepository<T, Integer> repository, Integer id) {
		if (repository == null || id == null) {
			throw new IllegalArgumentException("Neither of arguments can be null to successfully run query for an entity");
		}
		return repository.findById(id)
			.orElseThrow(() -> new ResourceNotFound(MessageCodeConstants.RESOURCE_NOT_FOUND));
	}
}
