package org.monjo.core.conversion;

import static org.junit.Assert.assertNotNull;
import static org.monjo.test.util.MongoDBUtil.getMongoDB;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.document.DirtWatcherProxifier;
import org.monjo.example.Category;
import org.monjo.example.PojoWithListInnerObject;
import org.monjo.example.Status;
import org.monjo.example.StatusConverter;
import org.monjo.test.util.MongoDBTest;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;


public class FinbdByExampleTest extends MongoDBTest {

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);
	}

	@Test
	public void shouldFindByExample() {
		PojoWithListInnerObject createMegaZordePojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		monjo.removeAll();
		monjo.insert(createMegaZordePojo);
		
		PojoWithListInnerObject createMegaZordePojo2 = new PojoWithListInnerObject();
		createMegaZordePojo2.addCategory(createMegaZordePojo.getCategories().get(0));
		PojoWithListInnerObject result = monjo.findByExample(createMegaZordePojo).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());
	}

	@Test
	public void shouldFindByExampleSoSo() {
		PojoWithListInnerObject createMegaZordePojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		monjo.removeAll();
		monjo.insert(createMegaZordePojo);

		List<Category> categories = createMegaZordePojo.getCategories();

		createMegaZordePojo = new PojoWithListInnerObject();
		Category findCategory = new Category();
		findCategory.setId(categories.get(0).getId());
		createMegaZordePojo.addCategory(findCategory);

		
		PojoWithListInnerObject result = monjo.findByExample(createMegaZordePojo).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());
	}

	@Test
	public void shouldFindByExampleSoSoWithProxy() {
		PojoWithListInnerObject createMegaZordePojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		monjo.removeAll();
		monjo.insert(createMegaZordePojo);
		
		List<Category> categories = createMegaZordePojo.getCategories();
		createMegaZordePojo = new PojoWithListInnerObject();
		Category findCategory = new Category();
		findCategory.setId(categories.get(0).getId());
		List<Category> list = new ArrayList<Category>();
		list.add(findCategory);
		
		PojoWithListInnerObject megaZordProxified = DirtWatcherProxifier.proxify(new PojoWithListInnerObject());
		megaZordProxified.setCategories(list);
		PojoWithListInnerObject result = monjo.findByExample(megaZordProxified).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());
	}

}
