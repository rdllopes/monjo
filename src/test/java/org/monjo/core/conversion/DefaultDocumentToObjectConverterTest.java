package org.monjo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.monjo.test.util.HamcrestPatch.classEqualTo;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.monj.example.SimplePOJO;
import org.monj.example.SimplePOJOWithStringId;
import org.monjo.test.util.MongoDBTest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultDocumentToObjectConverterTest extends MongoDBTest {
	

	@Before
	public void setUp() throws Exception {
		MongoDBTest.getMonjoCollection().drop();
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowIllegalStateExceptionIfToMethodIsCalledWithouthCallingFromMethodFirst() throws Exception {
		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		
		converter.to();
	}
	
	@Test
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		document.put("aField", "aFieldValue");
		document.put("anotherField", "anotherFieldValue");
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));

		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		

		SimplePOJO convertedObject = converter.from(docFromMongo).to();
		assertThat(convertedObject.getaField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(equalTo("anotherFieldValue")));
	}
	
	@Test
	public void shouldOnlyConvertFieldIfTheDocumentContainsAMatchingField() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		document.put("aField", "aFieldValue");
		saveToMongo(document);
		
		DBObject doc = spy(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));

		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		

		SimplePOJO convertedObject = converter.from(docFromMongo).to();
		assertThat(convertedObject.getaField(), is(equalTo("aFieldValue")));
		assertThat(convertedObject.getAnotherField(), is(nullValue()));
		
		verify(doc, never()).get("anotherField");
	}
	
	@Test
	public void shouldConvertNumericValues() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		document.put("anIntegerField", 42);
		document.put("aLongField", 43L);
		document.put("aDoubleField", 44.0);
		document.put("aFloatField", 45.0f);
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		
		
		SimplePOJO convertedObject = converter.from(docFromMongo).to();
		assertThat(convertedObject.getAnIntegerField(), is(equalTo(42)));
		assertThat(convertedObject.getaLongField(), is(equalTo(43L)));
		assertThat(convertedObject.getaDoubleField(), is(equalTo(44.0)));
	}

	@Test
	public void shouldPopulateIdWithMongosGeneratedIdValue() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		saveToMongo(document);
		
		Object documentId = document.get("_id");
		DBObject docFromMongo = getFromMongo(documentId);

		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		

		SimplePOJO convertedObject = converter.from(docFromMongo).to();
		assertThat(convertedObject.getId(), is(equalTo(documentId)));
	}
	
	@Test
	public void shouldPopulateStringId() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		document.put("_id", "abcd1234");
		saveToMongo(document);
		
		Object documentId = document.get("_id");
		DBObject docFromMongo = getFromMongo(documentId);
		
		DocumentToObjectConverter<SimplePOJOWithStringId> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJOWithStringId.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());				
		SimplePOJOWithStringId convertedObject = converter.from(docFromMongo).to();
		Class<?> idClass = convertedObject.getId().getClass();
		assertThat(idClass, classEqualTo(String.class));
		assertThat(convertedObject.getId(), is(equalTo(documentId)));
	}

	@Test
	public void shouldNotPopulateTransientFields() throws IllegalArgumentException, Exception {
		DBObject document = new BasicDBObject();
		document.put("aTransientField", "transient");
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		DocumentToObjectConverter<SimplePOJO> converter = MonjoConverterFactory.getInstance().getDefaultDocumentConverter(SimplePOJO.class);
		converter.setNamingStrategy(new DefaultNamingStrategy());		

		SimplePOJO simplePOJO = converter.from(docFromMongo).to();
		assertThat(simplePOJO.getaTransientField(), is(nullValue()));
	}
	
		
}
