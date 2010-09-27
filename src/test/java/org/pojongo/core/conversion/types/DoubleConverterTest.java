package org.pojongo.core.conversion.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pojongo.core.conversion.ConversionException;

public class DoubleConverterTest {
	
	private DoubleConverter converter;

	@Before
	public void setUp() {
		converter = new DoubleConverter();
	}
	
	@Test
	public void shouldConvertObjectToStringRepresentation() {
		Object object = new Double(42.0);
		Double asDouble = converter.fromObject(object);
		
		assertThat(asDouble, is(equalTo(42.0)));
	}
	
	@Test
	public void shouldReturnNullIfObjectIsNull() {
		Double asDouble = converter.fromObject(null);
		assertThat(asDouble, is(nullValue()));
	}

	@Test(expected=ConversionException.class)
	public void shouldThrowConversionExceptionIfTheValueCannotBeConvertedToLong() {
		converter.fromObject("not_a_number");
	}
	
}
