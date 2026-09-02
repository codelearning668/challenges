package sk.mkrajcovic.challenges.repository.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import sk.mkrajcovic.challenges.controller.InitHelper;
import sk.mkrajcovic.challenges.enums.MessageCodeConstants;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;
import sk.mkrajcovic.challenges.repository.persistence.CarRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EntityUtilsTest {

	private static final String EXPECTED_ERROR_MESSAGE = "Neither of arguments can be null to successfully run query for an entity";

	@LocalServerPort
	int port;

	@Autowired
	private InitHelper helper;

	@Autowired
	private CarRepository repository; 

	@BeforeEach
	void setup() {
		helper.init(port);
	}

	@Test
	void throwsIllegalArgumentExceptionWhenRepositoryIsNull() {
		var exception = assertThrows(IllegalArgumentException.class,
			() -> EntityUtils.getExistingEntityById(null, 1));

		assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
	}

	@Test
	void throwsIllegalArgumentExceptionWhenIdIsNull() {
		var exception = assertThrows(IllegalArgumentException.class,
			() -> EntityUtils.getExistingEntityById(repository, null));

		assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
	}

	@Test
	void throwsResourceNotFoundWhenEntityDoesNotExist() {
		var exception = assertThrows(ResourceNotFound.class,
			() -> EntityUtils.getExistingEntityById(repository, 99999));

		assertTrue(exception.getMessage().contains(MessageCodeConstants.RESOURCE_NOT_FOUND));
	}

}
