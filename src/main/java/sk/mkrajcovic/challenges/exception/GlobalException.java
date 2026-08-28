package sk.mkrajcovic.challenges.exception;

import java.util.Arrays;
import java.util.Objects;

public abstract class GlobalException extends RuntimeException {

	private static final long serialVersionUID = -1413853990238635218L;

	private final String code;
	private final transient Object[] args;

	protected GlobalException(String code, Throwable cause, Object... args) {
		super(code + ": " + Arrays.toString(args), cause);
		this.code = code;
		this.args = args != null ? Arrays.copyOf(args, args.length) : null;
	}

	public String getCode() {
		return code;
	}

	public Object[] getArgs() {
		return args != null ? Arrays.copyOf(args, args.length) : new Object[0];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GlobalException)) return false;
		GlobalException that = (GlobalException) o;
		return Objects.equals(code, that.code) && Arrays.equals(args, that.args);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(code);
		result = 31 * result + Arrays.hashCode(args);
		return result;
	}
}
