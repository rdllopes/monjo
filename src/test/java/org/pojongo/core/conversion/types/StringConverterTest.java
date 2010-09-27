package org.pojongo.core.conversion.types;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class StringConverterTest {
	
	private StringConverter converter;

	@Before
	public void setUp() {
		converter = new StringConverter();
	}
	
	@Test
	public void shouldConvertObjectToStringRepresentation() {
		Object object = new Object();
		String asString = converter.fromObject(object);
		
		assertThat(asString, is(equalTo(object.toString())));
	}
	
	@Test
	public void shouldReturnNullIfObjectIsNull() {
		String asString = converter.fromObject(null);
		assertThat(asString, is(nullValue()));
	}
	
}
