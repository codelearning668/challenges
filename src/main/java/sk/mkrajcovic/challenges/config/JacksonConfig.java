package sk.mkrajcovic.challenges.config;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Configuration
public class JacksonConfig {

	/**
	 * Customizes Jackson's {@code ObjectMapper} to use the application's lap-time
	 * format for all {@link Duration} values.
	 * <p>
	 * Durations are deserialized from {@code m:ss.SSS}-style values and serialized
	 * back to the canonical {@code mm:ss.SSS} representation.<br>
	 * For example, {@code "1:20.1"} is interpreted as {@code 1:20.100} and
	 * serialized as {@code "01:20.100"}.
	 *
	 * @return a Jackson customizer registering the global {@link Duration}
	 *         serializer and deserializer
	 */
	@Bean
	Jackson2ObjectMapperBuilderCustomizer durationCustomizer() {
		return builder -> {
			builder.serializerByType(Duration.class, new DurationSerializer());

			builder.deserializerByType(Duration.class, new DurationDeserializer());
		};
	}

	/**
	 * Serializes a {@link Duration} as a racing lap time in the canonical
	 * {@code mm:ss.SSS} format.
	 * <p>
	 * The duration is represented with minute, second, and millisecond components.
	 * For example:
	 * <ul>
	 *  <li>{@code Duration.ofMillis(80100)} → {@code "01:20.100"}</li>
	 *  <li>{@code Duration.ofMillis(83123)} → {@code "01:23.123"}</li>
	 * </ul>
	 * <p>
	 * The serializer always produces three digits for milliseconds, ensuring a
	 * consistent representation across API responses.
	 */
	static class DurationSerializer extends JsonSerializer<Duration> {

		@Override
		public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider)
				throws IOException {

			long totalMillis = duration.toMillis();

			long minutes = totalMillis / 60_000;
			long remainingMillis = totalMillis % 60_000;

			long seconds = remainingMillis / 1_000;
			long milliseconds = remainingMillis % 1_000;

			generator.writeString(String.format("%02d:%02d.%03d", minutes, seconds, milliseconds));
		}
	}

	/**
	 * Deserializes a racing lap-time string into a {@link Duration}.
	 * <p>
	 * The accepted format is {@code m:ss.S}, {@code m:ss.SS}, or {@code m:ss.SSS}.
	 * Fractional seconds are interpreted as milliseconds and padded to three digits
	 * when necessary:
	 * <ul>
	 *  <li>{@code "1:20.1"} -> {@code 01:20.100}</li>
	 *  <li>{@code "1:20.10"} -> {@code 01:20.100}</li>
	 *  <li>{@code "1:20.100"} -> {@code 01:20.100}</li>
	 *  <li>{@code "01:23.456"} -> {@code 01:23.456}</li>
	 * </ul>
	 * <p>
	 * The resulting {@link Duration} is independent of the textual representation,
	 * allowing lap times to be compared and calculated using the standard
	 * {@code Duration} API.
	 *
	 * @throws IOException if the JSON value is not a valid lap-time string
	 */
	static class DurationDeserializer extends JsonDeserializer<Duration> {

		private static final Pattern PATTERN = Pattern.compile("^(\\d+):(\\d{2})\\.(\\d{1,3})$");

		@Override
		public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException {

			String value = parser.getValueAsString();

			if (value == null) {
				return null;
			}

			Matcher matcher = PATTERN.matcher(value);

			if (!matcher.matches()) {
				throw context.weirdStringException(value, Duration.class,
						"Expected duration in format m:ss.S, m:ss.SS or m:ss.SSS, e.g. 1:23.456");
			}

			long minutes = Long.parseLong(matcher.group(1));
			long seconds = Long.parseLong(matcher.group(2));

			String millisecondsPart = matcher.group(3);

			long milliseconds = switch (millisecondsPart.length()) {
			case 1 -> Long.parseLong(millisecondsPart) * 100;
			case 2 -> Long.parseLong(millisecondsPart) * 10;
			case 3 -> Long.parseLong(millisecondsPart);
			default -> throw new IllegalStateException("Unexpected milliseconds length");
			};

			return Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(milliseconds);
		}
	}
}
