package org.monjo.core.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.monjo.test.util.MongoDBUtil.getMongoDB;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.Monjo;
import org.monjo.core.MonjoCursor;
import org.monjo.example.AnotherPojo;
import org.monjo.example.Category;
import org.monjo.example.ComplexPojo;
import org.monjo.example.ListWithin;
import org.monjo.example.PojoWithListInnerObject;
import org.monjo.example.User;
import org.monjo.test.util.MongoDBTest;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;


public class ComplexSaveTest extends MongoDBTest{
	
	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultObjectConverter(ListWithin.class);
	}

	@Test
	public void shouldSaveElementWithStringList(){
		ListWithin pojo = new ListWithin();
		pojo.addName(42);
		pojo.addName(43);
		
		Monjo<ObjectId, ListWithin> monjo = new Monjo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = monjo.save(pojo);
		ListWithin listWithin = monjo.findOne(objectId);
		List<String> strings = listWithin.getNames();
		Assert.assertTrue(strings.contains("42"));
		Assert.assertTrue(strings.contains("43"));
	}
	
	@Test
	public void shouldSaveElementWithIntegerList(){
		ListWithin pojo = new ListWithin();
		pojo.addGroup(10);
		pojo.addGroup(20);
		
		Monjo<ObjectId, ListWithin> monjo = new Monjo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = monjo.save(pojo);
		ListWithin listWithin = monjo.findOne(objectId);
		List<Integer> integers = listWithin.getGroups();
		Assert.assertTrue(integers.contains(10));
		Assert.assertTrue(integers.contains(20));
	}
	
	@Test
	public void shouldNotUseRef() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);

		monjoComplex.removeAll();
		monjoComplex.insert(pojo);
		assertNotNull(pojo.getCategories().get(0).getId());
		
		MonjoCursor<PojoWithListInnerObject> monjoCursor = monjoComplex.find();
		PojoWithListInnerObject complex = monjoCursor.toList().get(0);
		assertNotNull(complex.getCategories().get(0).getId());
	}

	@Test
	public void shouldAddNewCategory() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);
		monjoComplex.removeAll();
		
		monjoComplex.insert(pojo);
		Category otherCategory = new Category();
		otherCategory.setName("Other Category");
		pojo.addCategory(otherCategory);
		
		monjoComplex.update(pojo);		
		MonjoCursor<PojoWithListInnerObject> monjoCursor = monjoComplex.find();
		PojoWithListInnerObject complex = monjoCursor.toList().get(0);
		assertEquals(2, complex.getCategories().size());
	}

	@Test
	public void shouldAddNewCategoryButIHaveOnlyAnId() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> monjoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);
		monjoComplex.removeAll();
		
		monjoComplex.insert(pojo);
		
		PojoWithListInnerObject anotherPojo  = new PojoWithListInnerObject();
		anotherPojo.setId(pojo.getId());
		Category otherCategory = new Category();
		otherCategory.setName("Other Category");
		anotherPojo.addCategory(otherCategory);
		
		monjoComplex.updateWithAddSet(anotherPojo);
		
		MonjoCursor<PojoWithListInnerObject> monjoCursor = monjoComplex.find();
		PojoWithListInnerObject complex = monjoCursor.toList().get(0);
		assertEquals(2, complex.getCategories().size());
	}

	
	
	@Test
	public void shouldNotUseRef2() throws Exception{
		User user = new User();
		user.setName("NewCategory");

		AnotherPojo anotherPojo = PojoBuilder.createAnotherPojo(user);
		
		Monjo<ObjectId, AnotherPojo> monjoComplex = new Monjo<ObjectId, AnotherPojo>(getMongoDB(), AnotherPojo.class);

		monjoComplex.removeAll();

		monjoComplex.insert(anotherPojo);
		
		MonjoCursor<AnotherPojo> monjoCursor = monjoComplex.find();
		AnotherPojo anotherPojo2 = monjoCursor.toList().get(0);
		assertEquals("NewCategory", anotherPojo2.getUser().getName());
	}
	
	
	
	@Test
	public void shouldUseRef() throws Exception{
		
		Category category = new Category();
		category.setName("NewCategory");
		Monjo<ObjectId, Category> monjoCategory = new Monjo<ObjectId, Category>(getMongoDB(), Category.class);
		monjoCategory.insert(category);

		ComplexPojo complexPojo = PojoBuilder.createComplexPojo(category);
//		complexPojo.setCategories(categories);
		Monjo<ObjectId, ComplexPojo> monjoComplex = new Monjo<ObjectId, ComplexPojo>(getMongoDB(), ComplexPojo.class);

		monjoComplex.removeAll();
		monjoCategory.removeAll();

		monjoComplex.insert(complexPojo);
		
		MonjoCursor<ComplexPojo> monjoCursor = monjoComplex.find();
		ComplexPojo complex = monjoCursor.toList().get(0);
		assertEquals(category.getId(), complex.getCategory().getId());
	//	assertEquals(category.getId(), complex.getCategories().get(0).getId());
		
	}
	
}
