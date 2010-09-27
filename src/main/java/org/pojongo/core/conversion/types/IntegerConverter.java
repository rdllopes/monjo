package org.pojongo.core.conversion.types;

import org.pojongo.core.conversion.ConversionException;

/**
 * Converter for <code>Integer</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class IntegerConverter implements TypeConverter<Integer> {

	@Override
	public Integer fromObject(Object objectToConvert) {
		if (objectToConvert == null) {
			return null;
		}
		
		String valueAsString = objectToConvert.toString();
		try {
			return Integer.valueOf(valueAsString);
		} catch (NumberFormatException e) {
			throw new ConversionException("could not convert [" + valueAsString + "] to Integer");
		}
	}

}
