package org.monjo.core.conversion;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.example.Category;
import org.monjo.example.PojoWithListInnerObject;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;

public class FinbdByExampleTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}

	@Test
	public void shouldFindByExample() {
		PojoWithListInnerObject createMegaZordePojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		pojongo.removeAll();
		pojongo.insert(createMegaZordePojo);
		createMegaZordePojo.setId(null);
		PojoWithListInnerObject result = pojongo.findByExample(createMegaZordePojo).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());
	}

	@Test
	public void shouldFindByExampleSoSo() {
		PojoWithListInnerObject createMegaZordePojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		pojongo.removeAll();
		pojongo.insert(createMegaZordePojo);
		createMegaZordePojo.setId(null);
		List<Category> categories = createMegaZordePojo.getCategories();
		Category category = categories.get(0);
		category.setName(null);
		PojoWithListInnerObject result = pojongo.findByExample(createMegaZordePojo).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());
	}


}
