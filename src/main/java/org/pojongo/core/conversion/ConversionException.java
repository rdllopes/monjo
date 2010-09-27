package org.pojongo.core.conversion;

/**
 * Exception indicating that something wrong happened while converting objects.
 * 
 * @author Caio Filipini
 */
public class ConversionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public ConversionException(String message) {
		super(message);
	}

}
