package sk.mkrajcovic.challenges.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;

import com.fasterxml.jackson.databind.ObjectMapper;

class DurationSerializerTest {

	private final ObjectMapper objectMapper = createObjectMapper();

	@Test
	void serializesPositiveDurationInCanonicalLapTimeFormat() throws Exception {
		assertEquals("\"01:20.100\"", objectMapper.writeValueAsString(Duration.ofMillis(80_100)));
	}

	@Test
	void serializesNegativeDurationWithOneLeadingMinusSign() throws Exception {
		assertEquals("\"-00:01.908\"", objectMapper.writeValueAsString(Duration.ofMillis(-1_908)));
	}

	@Test
	void serializesNegativeDurationAcrossMinuteBoundaryWithOneLeadingMinusSign() throws Exception {
		assertEquals("\"-01:23.345\"", objectMapper.writeValueAsString(Duration.ofMillis(-83_345)));
	}

	private ObjectMapper createObjectMapper() {
		Jackson2ObjectMapperBuilderCustomizer customizer = new JacksonConfig().durationCustomizer();
		var builder = new org.springframework.http.converter.json.Jackson2ObjectMapperBuilder();
		customizer.customize(builder);
		return builder.build();
	}
}
