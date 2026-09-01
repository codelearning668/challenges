package sk.mkrajcovic.challenges.repository.util;

import org.springframework.data.repository.CrudRepository;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.enums.MessageCodeConstants;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;

@NoArgsConstructor(access = AccessLevel.NONE)
public class EntityUtils {

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
