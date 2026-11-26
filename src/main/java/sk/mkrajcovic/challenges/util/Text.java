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
	 * Checks whether the input text is not blank (not null, empty or consist of
	 * only whitespaces).
	 *
	 * @param input text to evaluate
	 * @return {@code true}, if the text has at least one non-whitespace char
	 */
	public static boolean isNotBlank(CharSequence input) {
		return org.apache.commons.lang3.StringUtils.isNotBlank(input);
	}

	/**
	 * Checks whether at least one input text is blank (null, empty or contains
	 * only whitespaces).
	 *
	 * @param inputs inputs to evaluate
	 * @return {@code true} when at least one input is blank
	 */
	public static boolean isAnyBlank(CharSequence... inputs) {
		return org.apache.commons.lang3.StringUtils.isAnyBlank(inputs);
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

	/**
	 * Returns either the passed in String, or if the String is whitespace, empty
	 * ("") or {@code null}, the value of {@code defaultString}.
	 *
	 * @param string the String to check, may be null
	 * @param defaultString the default String to return if the input is whitespace,
	 *                      empty ("") or {@code null}, may be null
	 * @return the passed in String, or the default
	 */
    public static String defaultIfBlank(String string, String defaultString) {
    	return org.apache.commons.lang3.StringUtils.defaultIfBlank(string, defaultString);
    }

}
