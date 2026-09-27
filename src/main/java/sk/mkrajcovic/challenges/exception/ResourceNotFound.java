package sk.mkrajcovic.challenges.exception;

/**
 * Exception indicating that a requested resource could not be found.
 * <p>
 * The resource may originate from a persistent store, an in-memory collection,
 * a static definition, or another source used by the application.
 */
public class ResourceNotFound extends ClientException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFound(String code) {
		super(code);
	}

	public ResourceNotFound(String code, Object... args) {
		super(code, args);
	}

	public ResourceNotFound(String code, Throwable cause, Object... args) {
		super(code, cause, args);
	}
}
