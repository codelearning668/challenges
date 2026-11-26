package sk.mkrajcovic.challenges.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

class ResourceLoaderTest {

	@Test
	void testLoadResourceSuccess() {
		String resourceName = "test-resource.txt";
		Resource resource = ResourceLoader.loadResource(resourceName);
		assertNotNull(resource, "Resource should not be null");
		assertTrue(resource.exists(), "Resource should exist");
	}

	@Test
	void testLoadResourceFailure() {
		String resourceName = "non-existent-resource.txt";
		Exception exception = assertThrows(IllegalArgumentException.class, () -> ResourceLoader.loadResource(resourceName));
		String expectedMessage = "Unable to find classpath:non-existent-resource.txt on classpath";
		Assertions.assertTrue(exception.getMessage().contains(expectedMessage));
	}

}
