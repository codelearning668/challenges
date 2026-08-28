package sk.mkrajcovic.challenges.exception;

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
