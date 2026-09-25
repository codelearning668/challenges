package sk.mkrajcovic.challenges.controller.converter;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import sk.mkrajcovic.challenges.enums.SimulatorType;
import sk.mkrajcovic.challenges.exception.ResourceNotFound;

/**
 * Converts simulator names provided as URI path variables to
 * {@link SimulatorType} values.
 * <p>
 * The conversion is case-insensitive. An unknown simulator name results in a
 * {@link ResourceNotFound}.
 */
@Component
public class SimulatorTypeConverter implements Converter<String, SimulatorType> {

	@Override
	public SimulatorType convert(String value) {
		try {
			var enumString = value.replace("-", "_").toUpperCase(Locale.ROOT);
			return SimulatorType.valueOf(enumString);
		} catch (IllegalArgumentException iaex) {
			throw new ResourceNotFound("unknown simulator name");
		}
	}
}
