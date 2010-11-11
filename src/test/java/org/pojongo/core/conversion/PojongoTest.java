package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class PojongoTest extends MongoDBTest{

	private ObjectToDocumentConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = PojongoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultDocumentConverter();
	}
	
	@Test
	public void deveriaGravarElemento(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setALongField(43L);
		pojo.setADoubleField(44.0);
		
		Pojongo<ObjectId> pojongo = new Pojongo<ObjectId>();
		ObjectId objectId = pojongo.save(getPojongoCollection(), pojo);
		
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));
		
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
	public void deveriaAtualizarElemento(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setALongField(43L);
		pojo.setADoubleField(44.0);
		
		Pojongo<ObjectId> pojongo = new Pojongo<ObjectId>();
		ObjectId objectId = pojongo.save(getPojongoCollection(), pojo);

		pojo = new SimplePOJO();
		pojo.setADoubleField(45.0);
		pojo.setId(objectId);
		
		pojongo.save(getPojongoCollection(), pojo);
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));		

		Class anIntegerFieldClass = document.get("anIntegerField").getClass();
		assertThat(anIntegerFieldClass, is(equalTo(Integer.class)));
		assertThat((Integer) document.get("anIntegerField"), is(equalTo(42)));

		Class aLongFieldClass = document.get("aLongField").getClass();
		assertThat(aLongFieldClass, is(equalTo(Long.class)));
		assertThat((Long) document.get("aLongField"), is(equalTo(43L)));

		Class aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, is(equalTo(Double.class)));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(45.0)));

	}
	
}
