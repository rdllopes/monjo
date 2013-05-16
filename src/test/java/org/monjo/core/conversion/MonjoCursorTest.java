package org.monjo.core.conversion;

import static org.junit.Assert.assertEquals;

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

import contrib.org.hibernate.cfg.DefaultNamingStrategy;


public class MonjoCursorTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}

	@Test
	public void deveriaLimitarResultados() throws Exception {
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		insertPojoCollection(monjo);
		List<SimplePOJO> list = monjo.find().limit(5).toList();
		assertEquals(5, list.size());
	}

	private void insertPojoCollection(Monjo<ObjectId, SimplePOJO> monjo) {
		for (int i = 0; i < 30; i++) {
			SimplePOJO pojo = new SimplePOJO();
			pojo.setAnIntegerField(i);
			pojo.setaLongField(43L);
			pojo.setaDoubleField(44.0);

			monjo.insert(pojo);
		}
	}

	@Test
	public void deveriaComecarNoQuintoResultado() throws Exception {
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		insertPojoCollection(monjo);
		List<SimplePOJO> list = monjo.find().limit(5).toList();
		List<SimplePOJO> list2 = monjo.find().skip(4).limit(1).toList();
		assertEquals(list.get(4), list2.get(0));
	}

	@Test
	public void deveriaContarDocumentosColecao() {
		Monjo<ObjectId, SimplePOJO> monjo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		insertPojoCollection(monjo);
		assertEquals(30, monjo.getCount());
	}
}
