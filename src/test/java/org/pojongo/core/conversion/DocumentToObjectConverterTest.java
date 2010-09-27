package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
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
	
	@Test
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() {
		DBObject document = mock(DBObject.class);
		when(document.get("aField")).thenReturn("aFieldValue");
		when(document.get("anotherField")).thenReturn("anotherFieldValue");
		
		SimplePOJO convertedObject = converter.from(document).to(SimplePOJO.class);
		assertThat(convertedObject.getAField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(equalTo("anotherFieldValue")));
	}
}
