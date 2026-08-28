package sk.mkrajcovic.challenges.controller.util;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Use this class to conveniently create {@link ResponseEntity} objects after
 * creating a new resource. This class eases the creation of HTTP {@code Link}
 * header pointing to the newly created resource, and automates setting the
 * {@link HttpStatus#CREATED} as the response status code.
 */
public class CreatedResponseEntity extends ResponseEntity<Object> {

	private CreatedResponseEntity(URI uri) {
		super(buildResourceLocationHeader(uri), HttpStatus.CREATED);
	}

	private static HttpHeaders buildResourceLocationHeader(URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);
		return headers;
	}

	/**
	 * @param uriString the location pointing to the newly created resource cannot be {@code null}
	 * @param args      for the {@code uriString} place-holders, e.g. may be {@code id} of the resource
	 * @return the {@link ResponseEntity} object representing newly created resource
	 */
	public static CreatedResponseEntity create(String uriString, Object... args) {
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
		UriComponents uriComponent = UriComponentsBuilder.fromPath(uriString).build();
		URI uri = builder.path(uriComponent.getPath()).query(uriComponent.getQuery()).buildAndExpand(args).toUri(); // NOSONAR uriComponent.getPath() won't be null
		return new CreatedResponseEntity(uri);
	}
}
