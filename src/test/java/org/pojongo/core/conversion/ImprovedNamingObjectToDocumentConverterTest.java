package org.pojongo.core.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.pojongo.test.util.HamcrestPatch.classEqualTo;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.example.SimplePOJO;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.DBObject;

public class ImprovedNamingObjectToDocumentConverterTest extends MongoDBTest {

	private ObjectToDocumentConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = PojongoConverterFactory.getInstance().configure(new ImprovedNamingStrategy()).getDefaultObjectConverter();
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConvertASimpleDocumentWithStringFieldsToAJavaObject() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setaField("foo");
		pojo.setAnotherField("bar");
		
		DBObject document = converter.from(pojo).toDocument();
		
		assertThat(document.containsField("a_field"), is(true));
		Class a_fieldClass = document.get("a_field").getClass();
		assertThat(a_fieldClass, classEqualTo(String.class));
		assertThat((String) document.get("a_field"), is(equalTo("foo")));
		
		assertThat(document.containsField("another_field"), is(true));
		Class anotherFieldClass = document.get("another_field").getClass();
		assertThat(anotherFieldClass, classEqualTo(String.class));
		assertThat((String) document.get("another_field"), is(equalTo("bar")));
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void shouldConvertNumericValues() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		DBObject document = converter.from(pojo).toDocument();
		
		Class<?> anIntegerFieldClass = document.get("an_integer_field").getClass();
		assertThat(anIntegerFieldClass, classEqualTo(Integer.class));
		
		assertThat((Integer) document.get("an_integer_field"), is(equalTo(42)));
		
		Class a_long_fieldClass = document.get("a_long_field").getClass();
		assertThat(a_long_fieldClass, classEqualTo(Long.class));
		assertThat((Long) document.get("a_long_field"), is(equalTo(43L)));
		
		Class a_double_fieldClass = document.get("a_double_field").getClass();
		assertThat(a_double_fieldClass, classEqualTo(Double.class));
		assertThat((Double) document.get("a_double_field"), is(equalTo(44.0)));
	}

	@Test
	public void shouldOnlyPopulateFieldsThatAreNotNull() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		
		DBObject document = converter.from(pojo).toDocument();
		
		assertThat(document.containsField("an_integer_field"), is(true));
		assertThat(document.containsField("_id"), is(false));
		assertThat(document.containsField("a_field"), is(false));
		assertThat(document.containsField("another_field"), is(false));
		assertThat(document.containsField("a_long_field"), is(false));
		assertThat(document.containsField("a_double_field"), is(false));
	}
	

}
