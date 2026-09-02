package sk.mkrajcovic.challenges.test.util;

import org.springframework.http.HttpStatus;

public class HttpCodes {

	public static final int CREATED = HttpStatus.CREATED.value();
	public static final int OK = HttpStatus.OK.value();
	public static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
	public static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
	public static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
	public static final int FORBIDDEN = HttpStatus.FORBIDDEN.value();
	public static final int CONFLICT = HttpStatus.CONFLICT.value();

}
