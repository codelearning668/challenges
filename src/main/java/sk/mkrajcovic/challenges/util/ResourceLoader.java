package sk.mkrajcovic.challenges.util;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class provides convenient methods to load resources of various types
 * from the classpath and convert them into corresponding Java objects.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ResourceLoader {

	/**
	 * Loads a resource from the classpath based on the specified resource name.
	 * <p>
	 * This method constructs a resource path using the provided resource name and
	 * the classpath prefix, then attempts to load the resource. If the resource
	 * does not exist on the classpath, an exception is thrown.
	 *
	 * @param resourceName the name of the resource to load. This should be the path
	 *                     relative to the classpath. For example,
	 *                     "report_templates/Pripady.xlsx" would load a resource
	 *                     located at
	 *                     "src/main/resources/report_templates/Pripady.xlsx".
	 * @return a {@link Resource} object representing the loaded resource.
	 * @throws InfrastructureException if the resource cannot be found on the
	 *                                 classpath.
	 */
	public static Resource loadResource(String resourceName) {
		var resourcePath = "classpath:" + resourceName;
		var resource = new DefaultResourceLoader().getResource(resourcePath);
		if (!resource.exists()) {
			throw new IllegalArgumentException("Unable to find " + resourcePath + " on classpath");
		}
		return resource;
	}
}
