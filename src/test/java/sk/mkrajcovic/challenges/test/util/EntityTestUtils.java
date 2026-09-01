package sk.mkrajcovic.challenges.test.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import sk.mkrajcovic.challenges.model.BaseEntity;

@NoArgsConstructor(access = AccessLevel.NONE)
public class EntityTestUtils {

	public static <T extends BaseEntity> void setId(T entity, Integer id) {
		try {
			var field = entity.getClass().getSuperclass().getDeclaredField("id");
			field.setAccessible(true);
			field.set(entity, id);
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			ex.printStackTrace();
		}
	}

}
