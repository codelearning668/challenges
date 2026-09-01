package sk.mkrajcovic.challenges.exception;

import java.util.Arrays;

import lombok.EqualsAndHashCode;

/**
 * Exception representing a client error. Intended for use as validation errors
 * or any error tied to user input.
 */
@EqualsAndHashCode(callSuper = false)
public class ClientException extends RuntimeException {

	private static final long serialVersionUID = -1413853990238635218L;

	private final String code;
	private final transient Object[] args;

	protected ClientException(String code, Throwable cause, Object... args) {
		super(code + ": " + Arrays.toString(args), cause);
		this.code = code;
		this.args = args != null ? Arrays.copyOf(args, args.length) : null;
	}

	public ClientException(String code, Object... args) {
		this(code, null, args);
	}

	public ClientException(String code) {
		this(code, null, (Object[]) new String[0]);
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args != null ? Arrays.copyOf(args, args.length) : new Object[0];
	}

}
