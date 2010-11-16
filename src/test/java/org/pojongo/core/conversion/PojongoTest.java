package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.core.conversion.SimplePOJO.Status;
import org.pojongo.example.StatusConverter;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class PojongoTest extends MongoDBTest{

	@Before
	public void setUp() throws Exception {
		PojongoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultObjectConverter();
	}
	
	@Test
	public void deveriaGravarElemento(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);
		
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
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);

		pojo = new SimplePOJO();
		pojo.setaDoubleField(45.0);
		pojo.setId(objectId);
		
		pojongo.save(pojo);
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));		

		assertNull (document.get("anIntegerField"));
		assertNull (document.get("aLongField"));

		Class aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, is(equalTo(Double.class)));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(45.0)));

	}

	@Test
	public void deveriaEncontrarDocumentoInserido(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);

		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.insert(pojo);
		
		SimplePOJO simplePOJO = pojongo.findOne(new SimplePOJO(objectId));
		assertThat(pojo.getAnIntegerField(), is(simplePOJO.getAnIntegerField()));
		assertThat(pojo.getaLongField(), is(simplePOJO.getaLongField()));
		assertThat(pojo.getaDoubleField(), is(simplePOJO.getaDoubleField()));

	}

	@Test
	public void testEnumTypes() throws Exception {
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setStatus(Status.NEW);
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.insert(simplePOJO);
		ConvertUtils.register(new StatusConverter(), Status.class);
		SimplePOJO simplePOJO2 = pojongo.findOne(simplePOJO.getId());
		assertEquals(simplePOJO.getStatus(),simplePOJO2.getStatus());
		
	}
	

	
}
