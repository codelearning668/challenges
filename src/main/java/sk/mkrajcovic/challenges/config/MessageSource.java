package sk.mkrajcovic.challenges.config;

import java.util.Locale;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

/**
 * Converter used to resolve short BL codes to user friendly messages based on
 * translations located in: <code>resources/messages.properties</code>
 *
 * @author mkrajcovicux
 */
@Component
public class MessageSource extends ReloadableResourceBundleMessageSource {

	private static final Locale DEFAULT_LOCALE = Locale.getDefault();

	public MessageSource() {
		setBasename("classpath:messages");
		setDefaultEncoding("UTF-8");
		setUseCodeAsDefaultMessage(true);
	}

	/**
	 * Retrieves the translation from the dictionary based on the provided
	 * {@code code}.<br>
	 * If not found, the {@code code} itself is returned.
	 *
	 * @param code - a key rather, of the message for which the translation is
	 *             required
	 * @return translation or the {@code code} itself, never {@code null}
	 */
	public String getMessage(String code) {
		return super.getMessage(code, null, DEFAULT_LOCALE);
	}

	/**
	 * Retrieves the translation from the dictionary based on the provided
	 * {@code code} and complementary arguments that should be fitted into the
	 * translation place-holders.<br>
	 * If not found, the {@code code} itself is returned.
	 *
	 * @param code - a key rather, of the message for which the translation is
	 *             required
	 * @param args - complementary arguments to meaningfully fill the message
	 *             translation
	 * @return translation or the {@code code} itself, never {@code null}
	 */
	public String getMessage(String code, Object... args) {
		return super.getMessage(code, args, DEFAULT_LOCALE);
	}
}
