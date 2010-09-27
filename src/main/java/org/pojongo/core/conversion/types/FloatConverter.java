package org.pojongo.core.conversion.types;

import org.pojongo.core.conversion.ConversionException;

/**
 * Converter for <code>Float</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class FloatConverter implements TypeConverter<Float> {

	@Override
	public Float fromObject(Object objectToConvert) {
		if (objectToConvert == null) {
			return null;
		}
		
		String valueAsString = objectToConvert.toString();
		try {
			return Float.valueOf(valueAsString);
		} catch (NumberFormatException e) {
			throw new ConversionException("could not convert [" + valueAsString + "] to Float");
		}
	}

}
