package org.monjo.core.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
		
		Monjo<ObjectId, ListWithin> pojongo = new Monjo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = pojongo.save(pojo);
		ListWithin listWithin = pojongo.findOne(objectId);
		List<String> strings = listWithin.getNames();
		Assert.assertTrue(strings.contains("42"));
		Assert.assertTrue(strings.contains("43"));
	}
	
	@Test
	public void shouldSaveElementWithIntegerList(){
		ListWithin pojo = new ListWithin();
		pojo.addGroup(10);
		pojo.addGroup(20);
		
		Monjo<ObjectId, ListWithin> pojongo = new Monjo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = pojongo.save(pojo);
		ListWithin listWithin = pojongo.findOne(objectId);
		List<Integer> integers = listWithin.getGroups();
		Assert.assertTrue(integers.contains(10));
		Assert.assertTrue(integers.contains(20));
	}
	
	@Test
	public void shouldNotUseRef() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);

		pojongoComplex.removeAll();
		pojongoComplex.insert(pojo);
		assertNotNull(pojo.getCategories().get(0).getId());
		
		MonjoCursor<PojoWithListInnerObject> pojongoCursor = pojongoComplex.find();
		PojoWithListInnerObject complex = pojongoCursor.toList().get(0);
		assertNotNull(complex.getCategories().get(0).getId());
	}

	@Test
	public void shouldAddNewCategory() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);
		pojongoComplex.removeAll();
		
		pojongoComplex.insert(pojo);
		Category otherCategory = new Category();
		otherCategory.setName("Other Category");
		pojo.addCategory(otherCategory);
		
		pojongoComplex.update(pojo);		
		MonjoCursor<PojoWithListInnerObject> pojongoCursor = pojongoComplex.find();
		PojoWithListInnerObject complex = pojongoCursor.toList().get(0);
		assertEquals(2, complex.getCategories().size());
	}

	@Test
	public void shouldAddNewCategoryButIHaveOnlyAnId() throws Exception {
		PojoWithListInnerObject pojo = PojoBuilder.createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(),
				PojoWithListInnerObject.class);
		pojongoComplex.removeAll();
		
		pojongoComplex.insert(pojo);
		
		PojoWithListInnerObject anotherPojo  = new PojoWithListInnerObject();
		anotherPojo.setId(pojo.getId());
		Category otherCategory = new Category();
		otherCategory.setName("Other Category");
		anotherPojo.addCategory(otherCategory);
		
		pojongoComplex.updateWithAddSet(anotherPojo);
		
		MonjoCursor<PojoWithListInnerObject> pojongoCursor = pojongoComplex.find();
		PojoWithListInnerObject complex = pojongoCursor.toList().get(0);
		assertEquals(2, complex.getCategories().size());
	}

	
	
	@Test
	public void shouldNotUseRef2() throws Exception{
		User user = new User();
		user.setName("NewCategory");

		AnotherPojo anotherPojo = PojoBuilder.createAnotherPojo(user);
		
		Monjo<ObjectId, AnotherPojo> pojongoComplex = new Monjo<ObjectId, AnotherPojo>(getMongoDB(), AnotherPojo.class);

		pojongoComplex.removeAll();

		pojongoComplex.insert(anotherPojo);
		
		MonjoCursor<AnotherPojo> pojongoCursor = pojongoComplex.find();
		AnotherPojo anotherPojo2 = pojongoCursor.toList().get(0);
		assertEquals("NewCategory", anotherPojo2.getUser().getName());
	}
	
	
	
	@Test
	public void shouldUseRef() throws Exception{
		
		Category category = new Category();
		category.setName("NewCategory");
		Monjo<ObjectId, Category> pojongoCategory = new Monjo<ObjectId, Category>(getMongoDB(), Category.class);
		pojongoCategory.insert(category);

		ComplexPojo complexPojo = PojoBuilder.createComplexPojo(category);
//		complexPojo.setCategories(categories);
		Monjo<ObjectId, ComplexPojo> pojongoComplex = new Monjo<ObjectId, ComplexPojo>(getMongoDB(), ComplexPojo.class);

		pojongoComplex.removeAll();
		pojongoCategory.removeAll();

		pojongoComplex.insert(complexPojo);
		
		MonjoCursor<ComplexPojo> pojongoCursor = pojongoComplex.find();
		ComplexPojo complex = pojongoCursor.toList().get(0);
		assertEquals(category.getId(), complex.getCategory().getId());
	//	assertEquals(category.getId(), complex.getCategories().get(0).getId());
		
	}
	
}
