package org.pojongo.core.conversion.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pojongo.core.conversion.ConversionException;

public class LongConverterTest {
	
	private LongConverter converter;

	@Before
	public void setUp() {
		converter = new LongConverter();
	}
	
	@Test
	public void shouldConvertObjectToStringRepresentation() {
		Object object = new Long(42L);
		Long asLong = converter.fromObject(object);
		
		assertThat(asLong, is(equalTo(42L)));
	}
	
	@Test
	public void shouldReturnNullIfObjectIsNull() {
		Long asLong = converter.fromObject(null);
		assertThat(asLong, is(nullValue()));
	}

	@Test(expected=ConversionException.class)
	public void shouldThrowConversionExceptionIfTheValueCannotBeConvertedToLong() {
		converter.fromObject("not_a_number");
	}
	
}
