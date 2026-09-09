package sk.mkrajcovic.challenges.controller.converter;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import sk.mkrajcovic.challenges.exception.ResourceNotFound;
import sk.mkrajcovic.challenges.model.SimulatorType;

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
			return SimulatorType.valueOf(value.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException iaex) {
			throw new ResourceNotFound("unknown simulator name");
		}
	}
}
