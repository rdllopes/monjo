package org.pojongo.core.conversion.types;

import org.pojongo.core.conversion.ConversionException;

/**
 * Converter for <code>Double</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class DoubleConverter implements TypeConverter<Double> {

	@Override
	public Double fromObject(Object objectToConvert) {
		if (objectToConvert == null) {
			return null;
		}
		
		String valueAsString = objectToConvert.toString();
		try {
			return Double.valueOf(valueAsString);
		} catch (NumberFormatException e) {
			throw new ConversionException("could not convert [" + valueAsString + "] to Double");
		}
	}

}
