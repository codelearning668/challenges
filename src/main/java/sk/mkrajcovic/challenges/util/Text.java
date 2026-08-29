package sk.mkrajcovic.challenges.util;

import java.text.Normalizer;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for working with text.<br>
 * Encapsulates operations over character arrays and isolates the use of
 * 3rd-party libraries, providing a stable API that shields callers from any
 * underlying implementation changes.
 * <p>
 * This class is not intended to be instantiated.<br>
 * All methods are static.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Text {

	// TODO: check usage of lang3.StringUtils, wrap it here and use this class

	/**
	 * Normalizes a string for accent-insensitive and case-insensitive searching.
	 *
	 * <p>
	 * The normalization decomposes Unicode characters into their base characters
	 * and combining marks, removes the combining marks (diacritics), and converts
	 * the result to lowercase using {@link Locale#ROOT}.
	 *
	 * <p>
	 * For example, {@code "Štefánia"} is normalized to {@code "stefania"}.
	 *
	 * @param value the string to normalize; may be {@code null}
	 * @return the normalized string, or {@code null} if {@code value} is
	 *         {@code null}
	 */
	public static String normalizeForSearch(String value) {
		if (value == null) {
			return null;
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "")
			.toLowerCase();
	}

}
