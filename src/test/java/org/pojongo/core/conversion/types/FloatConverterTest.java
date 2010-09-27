package org.pojongo.core.conversion.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pojongo.core.conversion.ConversionException;

public class FloatConverterTest {
	
	private FloatConverter converter;

	@Before
	public void setUp() {
		converter = new FloatConverter();
	}
	
	@Test
	public void shouldConvertObjectToStringRepresentation() {
		Object object = new Float(42.0f);
		Float asFloat = converter.fromObject(object);
		
		assertThat(asFloat, is(equalTo(42.0f)));
	}
	
	@Test
	public void shouldReturnNullIfObjectIsNull() {
		Float asFloat = converter.fromObject(null);
		assertThat(asFloat, is(nullValue()));
	}

	@Test(expected=ConversionException.class)
	public void shouldThrowConversionExceptionIfTheValueCannotBeConvertedToLong() {
		converter.fromObject("not_a_number");
	}
	
}
