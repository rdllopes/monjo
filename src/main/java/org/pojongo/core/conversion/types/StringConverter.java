package org.pojongo.core.conversion.types;

/**
 * Converter for <code>String</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class StringConverter implements TypeConverter<String> {

	@Override
	public String fromObject(Object object) {
		if (object == null) {
			return null;
		}
		return object.toString();
	}

}
