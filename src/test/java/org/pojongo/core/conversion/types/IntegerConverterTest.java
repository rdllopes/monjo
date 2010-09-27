package org.pojongo.core.conversion.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pojongo.core.conversion.ConversionException;

public class IntegerConverterTest {
	
	private IntegerConverter converter;

	@Before
	public void setUp() {
		converter = new IntegerConverter();
	}
	
	@Test
	public void shouldConvertObjectToStringRepresentation() {
		Object object = new Integer(42);
		Integer asInteger = converter.fromObject(object);
		
		assertThat(asInteger, is(equalTo(42)));
	}
	
	@Test
	public void shouldReturnNullIfObjectIsNull() {
		Integer asInteger = converter.fromObject(null);
		assertThat(asInteger, is(nullValue()));
	}

	@Test(expected=ConversionException.class)
	public void shouldThrowConversionExceptionIfTheValueCannotBeConvertedToInteger() {
		converter.fromObject("not_a_number");
	}
	
}
