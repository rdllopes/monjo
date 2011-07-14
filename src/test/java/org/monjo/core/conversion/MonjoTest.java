package org.monjo.core.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.monjo.test.util.HamcrestPatch.classEqualTo;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.example.SimplePOJO;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;


public class MonjoTest extends MongoDBTest{

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);				
	}
	


	@Test
	public void deveriaAtualizarElemento(){
		SimplePOJO pojo = PojoBuilder.createSimplePojo();		
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.removeAll();
		ObjectId objectId = pojongo.save(pojo);

		pojo = new SimplePOJO();
		pojo.setaDoubleField(45.0);
		pojo.setId(objectId);
		
		pojongo.save(pojo);
		DBObject document = getMonjoCollection().findOne(new BasicDBObject("_id", objectId));

		// yes, yes. If you have used save all data will be erased!  
		assertNull (document.get("anIntegerField"));
		assertNull (document.get("aLongField"));

		Class<?> aDoubleFieldClass = document.get("aDoubleField").getClass();
		Class<Double> doubleClass = Double.class;
		assertThat(aDoubleFieldClass, classEqualTo(doubleClass));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(45.0)));

	}
	

	@Test
	public void testEnumTypes() throws Exception {
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setStatus(Status.NEW);
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.insert(simplePOJO);
		SimplePOJO simplePOJO2 = pojongo.findOne(simplePOJO.getId());
		assertEquals(simplePOJO.getStatus(),simplePOJO2.getStatus());
		
	}
	
	
	@Test
	public void shouldFindBySimpleExample() {
		SimplePOJO simplePOJO = PojoBuilder.createSimplePojo();
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.removeAll();
		pojongo.insert(simplePOJO);
		SimplePOJO result = pojongo.findByExample(simplePOJO).toList().get(0);
		assertNotNull(result.getId());		
	}
	
}