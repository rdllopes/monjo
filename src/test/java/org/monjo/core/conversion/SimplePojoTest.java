package org.monjo.core.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.monjo.test.util.HamcrestPatch.classEqualTo;
import static org.monjo.test.util.MongoDBUtil.getMongoDB;
import static org.monjo.test.util.MongoDBUtil.getMonjoCollection;

import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.example.SimplePOJO;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;

public class SimplePojoTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}

	@Test
	public void deveriaGravarElemento() {
		SimplePOJO pojo = PojoBuilder.createSimplePojo();
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = monjo.save(pojo);
		
		DBObject document = getMonjoCollection().findOne(new BasicDBObject("_id", objectId));
		Class<?> anIntegerFieldClass = document.get("anIntegerField").getClass();
		assertThat(anIntegerFieldClass, classEqualTo(Integer.class));
		assertThat((Integer) document.get("anIntegerField"), is(equalTo(42)));

		Class<?> aLongFieldClass = document.get("aLongField").getClass();
		assertThat(aLongFieldClass, classEqualTo(Long.class));
		assertThat((Long) document.get("aLongField"), is(equalTo(43L)));

		Class<?> aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, classEqualTo(Double.class));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(44.0)));
	}

	@Test
	public void deveriaEncontrarDocumentoInserido() {
		SimplePOJO pojo = PojoBuilder.createSimplePojo();

		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = monjo.insert(pojo);

		SimplePOJO simplePOJO = monjo.findOneByExample(new SimplePOJO(objectId));
		compareTwoSimplePojos(pojo, simplePOJO);

	}

	@Test
	public void deveriaFiltrarUsandoIn() throws Exception{
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		SimplePOJO pojo = createSimplePojo();
		
		SimplePOJO pojo2 = new SimplePOJO();
		pojo2.setAnIntegerField(2);
		pojo2.setaLongField(43L);
		pojo2.setaDoubleField(44.0);
		
		SimplePOJO pojo3 = new SimplePOJO();
		pojo3.setAnIntegerField(3);
		pojo3.setaLongField(43L);
		pojo3.setaDoubleField(44.0);
		
		monjo.insert(pojo);
		monjo.insert(pojo2);
		monjo.insert(pojo3);
		
		BasicDBList inValues = new BasicDBList();
		inValues.add(1);
		inValues.add(2);
		
		BasicDBObject criteria = new BasicDBObject("anIntegerField", new BasicDBObject("$in", inValues));

		List<SimplePOJO> list = monjo.findBy(criteria).toList();
 		
		assertEquals(2, list.size());
	}

	public static SimplePOJO createSimplePojo() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(1);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		return pojo;
	}

	public static void compareTwoSimplePojos(SimplePOJO pojo, SimplePOJO simplePOJO) {
		assertThat(pojo.getAnIntegerField(), is(simplePOJO.getAnIntegerField()));
		assertThat(pojo.getaLongField(), is(simplePOJO.getaLongField()));
		assertThat(pojo.getaDoubleField(), is(simplePOJO.getaDoubleField()));
	}

	
	
	@Test
	public void deveriaRemover(){
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(123);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		monjo.insert(pojo);
		
		BasicDBObject criteria = new BasicDBObject("anIntegerField", 123);
		
		List<SimplePOJO> list = monjo.findBy(criteria).toList();
		
		monjo.removeByCriteria(criteria);
		
		List<SimplePOJO> list2 = monjo.findBy(criteria).toList();
		
		assertArrayEquals(new Integer[]{1,0}, new Integer[]{list.size(), list2.size()});
	}
	


}
