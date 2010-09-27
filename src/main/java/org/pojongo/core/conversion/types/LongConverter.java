package org.pojongo.core.conversion.types;

import org.pojongo.core.conversion.ConversionException;

/**
 * Converter for <code>Long</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class LongConverter implements TypeConverter<Long> {

	@Override
	public Long fromObject(Object objectToConvert) {
		if (objectToConvert == null) {
			return null;
		}
		
		String valueAsString = objectToConvert.toString();
		try {
			return Long.valueOf(valueAsString);
		} catch (NumberFormatException e) {
			throw new ConversionException("could not convert [" + valueAsString + "] to Long");
		}
	}

}
