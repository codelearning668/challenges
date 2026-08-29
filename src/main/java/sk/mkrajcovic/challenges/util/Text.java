package sk.mkrajcovic.challenges.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Objects;

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

	/**
	 * Checks whether the input text is blank (null, empty or contains
	 * only whitespaces).
	 *
	 * @param input text to evaluate
	 * @return {@code true}, if the text is null or blank
	 */
	public static boolean isBlank(CharSequence input) {
		return org.apache.commons.lang3.StringUtils.isBlank(input);
	} 

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
	public static String normalizeForSearch(CharSequence value) {
		if (isBlank(value)) {
			return Objects.toString(value, null);
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD)
			.replaceAll("\\p{M}", "")
			.toLowerCase(Locale.ROOT);
	}

}
