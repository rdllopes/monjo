package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.mongodb.DBObject;

public class DocumentToObjectConverterTest {
	
	private DocumentToObjectConverter converter;

	@Before
	public void setUp() {
		converter = new DocumentToObjectConverter();
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfDocumentIsNull() {
		converter.from(null);
	}
	
	@Test
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() {
		DBObject document = mock(DBObject.class);
		when(document.containsField(anyString())).thenReturn(true);
		when(document.get("aField")).thenReturn("aFieldValue");
		when(document.get("anotherField")).thenReturn("anotherFieldValue");
		
		SimplePOJO convertedObject = converter.from(document).to(SimplePOJO.class);
		assertThat(convertedObject.getAField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(equalTo("anotherFieldValue")));
	}
	
	@Test
	public void shouldOnlyConvertFieldIfTheDocumentContainsAMatchingField() {
		DBObject document = mock(DBObject.class);
		when(document.containsField("aField")).thenReturn(true);
		when(document.get("aField")).thenReturn("aFieldValue");
		when(document.containsField("anotherField")).thenReturn(false);
		
		SimplePOJO convertedObject = converter.from(document).to(SimplePOJO.class);
		assertThat(convertedObject.getAField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(nullValue()));
		
		verify(document, never()).get("anotherField");
	}
	
}
