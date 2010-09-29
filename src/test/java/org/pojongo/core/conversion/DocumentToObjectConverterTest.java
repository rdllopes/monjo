package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DocumentToObjectConverterTest extends MongoDBTest {
	
	private DocumentToObjectConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new DocumentToObjectConverter();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfDocumentIsNull() {
		converter.from(null);
	}
	
	@Test
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() {
		DBObject document = new BasicDBObject();
		document.put("aField", "aFieldValue");
		document.put("anotherField", "anotherFieldValue");
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		
		SimplePOJO convertedObject = converter.from(docFromMongo).to(SimplePOJO.class);
		assertThat(convertedObject.getAField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(equalTo("anotherFieldValue")));
	}
	
	@Test
	public void shouldOnlyConvertFieldIfTheDocumentContainsAMatchingField() {
		DBObject document = new BasicDBObject();
		document.put("aField", "aFieldValue");
		saveToMongo(document);
		
		DBObject doc = spy(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		
		SimplePOJO convertedObject = converter.from(docFromMongo).to(SimplePOJO.class);
		assertThat(convertedObject.getAField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(nullValue()));
		
		verify(doc, never()).get("anotherField");
	}
	
	@Test
	public void shouldConvertNumericValues() {
		DBObject document = new BasicDBObject();
		document.put("anIntegerField", 42);
		document.put("aLongField", 43L);
		document.put("aDoubleField", 44.0);
		document.put("aFloatField", 45.0f);
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		
		SimplePOJO convertedObject = converter.from(docFromMongo).to(SimplePOJO.class);
		assertThat(convertedObject.getAnIntegerField(), is(equalTo(42)));
		assertThat(convertedObject.getALongField(), is(equalTo(43L)));
		assertThat(convertedObject.getADoubleField(), is(equalTo(44.0)));
	}

	@Test
	public void shouldPopulateIdWithMongosGeneratedIdValue() {
		DBObject document = new BasicDBObject();
		saveToMongo(document);
		
		Object documentId = document.get("_id");
		DBObject docFromMongo = getFromMongo(documentId);
		
		SimplePOJO convertedObject = converter.from(docFromMongo).to(SimplePOJO.class);
		assertThat(convertedObject.getId(), is(equalTo(documentId)));
	}
	
}
