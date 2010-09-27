package org.pojongo.core.conversion.types;

/**
 * Converter for <code>String</code> objects.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.types.TypeConverter
 */
public class StringConverter implements TypeConverter<String> {

	/**
	 * @see org.pojongo.core.conversion.types.TypeConverter#fromObject(java.lang.Object)
	 */
	@Override
	public String fromObject(Object object) {
		if (object == null) {
			return null;
		}
		return object.toString();
	}

}
