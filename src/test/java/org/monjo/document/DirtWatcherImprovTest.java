package org.monjo.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.monjo.test.util.MongoDBUtil.getMongoDB;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.core.conversion.MonjoConverterFactory;
import org.monjo.core.conversion.SimplePojoTest;
import org.monjo.example.SimplePOJO;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;

public class DirtWatcherImprovTest extends MongoDBTest {
	
	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}
	
	@Test
	public void shouldUpdateSomeNullFields() throws Exception {
		SimplePOJO pojo = SimplePojoTest.createSimplePojo();
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = monjo.save(pojo);

		SimplePOJO newPojo = new SimplePOJO();
		SimplePOJO pojo2 = DirtWatcherProxifier.proxify(newPojo);

		pojo2.setaLongField(42l);
		pojo2.setAnIntegerField(null);
		pojo2.setId(objectId);
		monjo.update(pojo2);
		
		SimplePOJO dbPojo = monjo.findOne(objectId);
		assertNull(dbPojo.getAnIntegerField());
		assertEquals(new Long(42l), dbPojo.getaLongField());
	}
}
