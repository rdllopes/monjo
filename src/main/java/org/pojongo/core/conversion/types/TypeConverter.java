package org.pojongo.core.conversion.types;

/**
 * Interface for type conversion operations.
 * 
 * @param <T> the type to convert to.
 * 
 * @author Caio Filipini
 */
public interface TypeConverter<T> {

	/**
	 * Converts the specified object to the type configured for the converter.
	 * 
	 * @param objectToConvert object to be converted.
	 * @return the <code>T</code> representation for the specified object.
	 */
	T fromObject(Object objectToConvert);

}