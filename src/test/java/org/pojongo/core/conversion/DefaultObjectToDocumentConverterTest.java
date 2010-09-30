package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.DBObject;

public class DefaultObjectToDocumentConverterTest extends MongoDBTest {
	
	private ObjectToDocumentConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new DefaultObjectToDocumentConverter();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfTheObjectToBeConvertedIsNull() {
		converter.from(null);
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAField("foo");
		pojo.setAnotherField("bar");
		
		DBObject document = converter.from(pojo).toDocument();
		
		assertThat(document.containsField("aField"), is(true));
		Class aFieldClass = document.get("aField").getClass();
		assertThat(aFieldClass, is(equalTo(String.class)));
		assertThat((String) document.get("aField"), is(equalTo("foo")));
		
		assertThat(document.containsField("anotherField"), is(true));
		Class anotherFieldClass = document.get("anotherField").getClass();
		assertThat(anotherFieldClass, is(equalTo(String.class)));
		assertThat((String) document.get("anotherField"), is(equalTo("bar")));
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConvertNumericValues() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setALongField(43L);
		pojo.setADoubleField(44.0);
		
		DBObject document = converter.from(pojo).toDocument();
		
		Class anIntegerFieldClass = document.get("anIntegerField").getClass();
		assertThat(anIntegerFieldClass, is(equalTo(Integer.class)));
		assertThat((Integer) document.get("anIntegerField"), is(equalTo(42)));
		
		Class aLongFieldClass = document.get("aLongField").getClass();
		assertThat(aLongFieldClass, is(equalTo(Long.class)));
		assertThat((Long) document.get("aLongField"), is(equalTo(43L)));
		
		Class aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, is(equalTo(Double.class)));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(44.0)));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPopulateIdIfDefined() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.generateId();
		ObjectId id = pojo.getId();
		
		DBObject document = converter.from(pojo).toDocument();
		
		assertThat(document.containsField("_id"), is(true));
		Class idFieldClass = document.get("_id").getClass();
		assertThat(idFieldClass, is(equalTo(ObjectId.class)));
		assertThat((ObjectId) document.get("_id"), is(equalTo(id)));
	}
	
	@Test
	public void shouldOnlyPopulateFieldsThatAreNotNull() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		
		DBObject document = converter.from(pojo).toDocument();
		
		assertThat(document.containsField("anIntegerField"), is(true));
		assertThat(document.containsField("_id"), is(false));
		assertThat(document.containsField("aField"), is(false));
		assertThat(document.containsField("anotherField"), is(false));
		assertThat(document.containsField("aLongField"), is(false));
		assertThat(document.containsField("aDoubleField"), is(false));
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void shouldPopulateStringIdIfDefined() {
		SimplePOJOWithStringId pojoWithStringId = new SimplePOJOWithStringId();
		pojoWithStringId.setId("abcd1234");
		
		DBObject document = converter.from(pojoWithStringId).toDocument();
		
		assertThat(document.containsField("_id"), is(true));
		Class idClass = document.get("_id").getClass();
		assertThat(idClass, is(equalTo(String.class)));
		assertThat((String) document.get("_id"), is(equalTo("abcd1234")));
	}

	@Test
	public void shouldPreserveIdTypeWhenSavingToMongo() {
		assertIdOfTypeStringIsPreserved();
		assertIfOfTypeObjectIdIsPreserved();
		assertGeneratedIfOfTypeObjectIdIsPreserved();
	}

	@SuppressWarnings("rawtypes")
	private void assertIfOfTypeObjectIdIsPreserved() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.generateId();
		ObjectId id = pojo.getId();
		
		DBObject document = converter.from(pojo).toDocument();
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(id);
		Class idClass = docFromMongo.get("_id").getClass();
		assertThat(idClass, is(equalTo(ObjectId.class)));
	}
	
	@SuppressWarnings("rawtypes")
	private void assertGeneratedIfOfTypeObjectIdIsPreserved() {
		SimplePOJO pojo = new SimplePOJO();
		
		DBObject document = converter.from(pojo).toDocument();
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo(document.get("_id"));
		Class idClass = docFromMongo.get("_id").getClass();
		assertThat(idClass, is(equalTo(ObjectId.class)));
	}

	@SuppressWarnings("rawtypes")
	private void assertIdOfTypeStringIsPreserved() {
		SimplePOJOWithStringId pojoWithStringId = new SimplePOJOWithStringId();
		pojoWithStringId.setId("abcd1234");
		
		DBObject document = converter.from(pojoWithStringId).toDocument();
		saveToMongo(document);
		
		DBObject docFromMongo = getFromMongo("abcd1234");
		Class idClass = docFromMongo.get("_id").getClass();
		assertThat(idClass, is(equalTo(String.class)));
	}
	
}
