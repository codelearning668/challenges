package sk.mkrajcovic.challenges.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Encapsulates general-purpose operations over objects and isolates callers
 * from any underlying implementation or 3rd-party utility libraries, providing
 * a stable API for value-related operations.
 * <p>
 * This class is not intended to be instantiated. <br>
 * All methods are static.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Value {

	/**
	 * Returns the first non-null value from the supplied values.
	 * <p>
	 * Values are evaluated from left to right. If both values are {@code null},
	 * {@code null} is returned.
	 *
	 * @param <T> the type of the values
	 * @param first first value to evaluate
	 * @param second second value to evaluate
	 * @return the first non-null value, or {@code null}
	 *         if both values are {@code null}
	 */
	public static <T> T firstNonNull(T first, T second) {
		return first != null ? first : second;
	}

}
