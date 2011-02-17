package org.monjo.core.conversion;

import static org.junit.Assert.assertTrue;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.monjo.example.SimplePOJO;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.example.SubClassPojo;
import org.monjo.test.util.MongoDBTest;

public class UpdateTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}

	@Test
	public void shouldUpdateComplexObject() {
		SimplePOJO fixture = PojoBuilder.createSimplePojo();
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo",
				new NullCommand<SimplePOJO>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(fixture);

		Monjo<ObjectId, SubClassPojo> pojongo2 = new Monjo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo",
				new NullCommand<SubClassPojo>());
		SubClassPojo classPojo = pojongo2.findOne(objectId);
		SimplePojoTest.compareTwoSimplePojos(fixture, classPojo);
	}

	@Test
	public void shouldUpdateSimpleObject() {
		Status thing = Status.Delta;

		SubClassPojo pojo = new SubClassPojo();
		pojo.setAnIntegerField(44);
		pojo.setaLongField(44L);
		pojo.setaDoubleField(44.0);
		pojo.setStatus(thing);
		String extraInfo = "this extra info";
		pojo.setExtraProperty(extraInfo);

		Monjo<ObjectId, SubClassPojo> pojongo = new Monjo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo",
				new NullCommand<SubClassPojo>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(pojo);

		SimplePOJO simplePOJO = PojoBuilder.createSimplePojo();
		simplePOJO.setId(objectId);

		Monjo<ObjectId, SimplePOJO> pojongo2 = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo",
				new NullCommand<SimplePOJO>());
		pojongo2.update(simplePOJO);

		SimplePOJO fixture = PojoBuilder.createSimplePojo();
		simplePOJO = pojongo.findOne(objectId);
		assertTrue(fixture.getaDoubleField().equals(simplePOJO.getaDoubleField()));
		assertTrue(fixture.getAnIntegerField().equals(simplePOJO.getAnIntegerField()));
		assertTrue(fixture.getaLongField().equals(simplePOJO.getaLongField()));
		assertTrue(Status.Delta.equals(simplePOJO.getStatus()));

		SubClassPojo classPojo = pojongo.findOne(objectId);
		assertTrue(fixture.getaDoubleField().equals(classPojo.getaDoubleField()));
		assertTrue(fixture.getAnIntegerField().equals(classPojo.getAnIntegerField()));
		assertTrue(fixture.getaLongField().equals(classPojo.getaLongField()));
		assertTrue(Status.Delta.equals(classPojo.getStatus()));
		assertTrue(extraInfo.equals(classPojo.getExtraProperty()));
	}

	@Test
	public void shouldNotLoseFields() {
		PojoBuilder.createMegaZordePojo();
	}

}
