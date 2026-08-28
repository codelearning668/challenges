package sk.mkrajcovic.challenges.enums;

/**
 * Centralized list of message codes.<br>
 * All of these are translated from definitions in
 * <code>messages.properties</code> by {@link MessageSource}
 *
 * @author mkrajcovicux
 */
public class MessageCodeConstants {

	private MessageCodeConstants() {
		throw new IllegalStateException("MessageCodeConstants was not designed to be instantiated");
	}

	public static final String UNEXPECTED_ERROR = "Error";
	public static final String STALE_UPDATE = "staleUpdate";
	public static final String RESOURCE_NOT_FOUND = "resourceNotFound";
}
