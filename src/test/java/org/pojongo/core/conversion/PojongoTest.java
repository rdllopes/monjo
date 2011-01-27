package org.pojongo.core.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.pojongo.test.util.HamcrestPatch.classEqualTo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.example.Category;
import org.pojongo.example.ComplexPojo;
import org.pojongo.example.PojoWithListInnerObject;
import org.pojongo.example.SimplePOJO;
import org.pojongo.example.SimplePOJO.Status;
import org.pojongo.example.StatusConverter;
import org.pojongo.example.SubClassPojo;
import org.pojongo.test.util.MongoDBTest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class PojongoTest extends MongoDBTest{

	@Before
	public void setUp() throws Exception {
		PojongoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultObjectConverter();
		ConvertUtils.register(new StatusConverter(), Status.class);				
	}
	
	@Test
	public void deveriaGravarElemento(){
		SimplePOJO pojo = createSimplePojo();
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);
		
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));
		
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

	private SimplePOJO createSimplePojo() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		return pojo;
	}

	@Test
	public void deveriaAtualizarElemento(){
		SimplePOJO pojo = createSimplePojo();
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);

		pojo = new SimplePOJO();
		pojo.setaDoubleField(45.0);
		pojo.setId(objectId);
		
		pojongo.save(pojo);
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));		

		assertNull (document.get("anIntegerField"));
		assertNull (document.get("aLongField"));

		Class<?> aDoubleFieldClass = document.get("aDoubleField").getClass();
		Class<Double> doubleClass = Double.class;
		assertThat(aDoubleFieldClass, classEqualTo(doubleClass));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(45.0)));

	}

	@Test
	public void deveriaEncontrarDocumentoInserido(){
		SimplePOJO pojo = createSimplePojo();

		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.insert(pojo);
		
		SimplePOJO simplePOJO = pojongo.findOne(new SimplePOJO(objectId));
		compareTwoSimplePojos(pojo, simplePOJO);

	}

	private void compareTwoSimplePojos(SimplePOJO pojo, SimplePOJO simplePOJO) {
		assertThat(pojo.getAnIntegerField(), is(simplePOJO.getAnIntegerField()));
		assertThat(pojo.getaLongField(), is(simplePOJO.getaLongField()));
		assertThat(pojo.getaDoubleField(), is(simplePOJO.getaDoubleField()));
	}
	
	@Test
	public void deveriaLimitarResultados() throws Exception{
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		
		assertEquals(5, list.size());
	}

	private void insertPojoCollection(Pojongo<ObjectId, SimplePOJO> pojongo) {
		for(int i = 0; i < 30; i++){
			SimplePOJO pojo = new SimplePOJO();
			pojo.setAnIntegerField(i);
			pojo.setaLongField(43L);
			pojo.setaDoubleField(44.0);
			
			pojongo.insert(pojo);
		}
	}
	
	@Test
	public void deveriaComecarNoQuintoResultado() throws Exception{
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		List<SimplePOJO> list2 = pojongo.find().skip(4).limit(1).toList();
			
		assertEquals(list.get(4), list2.get(0));
	}
	
	@Test
	public void deveriaContarDocumentosColecao(){
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
			
		assertEquals(30, pojongo.getCount());
	}
	
	@Test
	public void deveriaFiltrarUsandoIn() throws Exception{
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(1);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		SimplePOJO pojo2 = new SimplePOJO();
		pojo2.setAnIntegerField(2);
		pojo2.setaLongField(43L);
		pojo2.setaDoubleField(44.0);
		
		SimplePOJO pojo3 = new SimplePOJO();
		pojo3.setAnIntegerField(3);
		pojo3.setaLongField(43L);
		pojo3.setaDoubleField(44.0);
		
		pojongo.insert(pojo);
		pojongo.insert(pojo2);
		pojongo.insert(pojo3);
		
		BasicDBList inValues = new BasicDBList();
		inValues.add(1);
		inValues.add(2);
		
		BasicDBObject criteria = new BasicDBObject("anIntegerField", new BasicDBObject("$in", inValues));

		List<SimplePOJO> list = pojongo.findBy(criteria).toList();
 		
		assertEquals(2, list.size());
	}
	
	@Test
	public void deveriaRemover(){
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(123);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		pojongo.insert(pojo);
		
		BasicDBObject criteria = new BasicDBObject("anIntegerField", 123);
		
		List<SimplePOJO> list = pojongo.findBy(criteria).toList();
		
		pojongo.removeByCriteria(criteria);
		
		List<SimplePOJO> list2 = pojongo.findBy(criteria).toList();
		
		assertArrayEquals(new Integer[]{1,0}, new Integer[]{list.size(), list2.size()});
	}

	@Test
	public void testEnumTypes() throws Exception {
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setStatus(Status.NEW);
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.insert(simplePOJO);
		SimplePOJO simplePOJO2 = pojongo.findOne(simplePOJO.getId());
		assertEquals(simplePOJO.getStatus(),simplePOJO2.getStatus());
		
	}
	
	
	@Test
	public void shouldUseRef() throws Exception{
		
		Category category = new Category();
		category.setName("NewCategory");
		Pojongo<ObjectId, Category> pojongoCategory = new Pojongo<ObjectId, Category>(getMongoDB(), Category.class);
		pojongoCategory.insert(category);

		ComplexPojo complexPojo = new ComplexPojo();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		complexPojo.setCategory(category);
		complexPojo.setDescription("pojo complexo");
//		complexPojo.setCategories(categories);
		Pojongo<ObjectId, ComplexPojo> pojongoComplex = new Pojongo<ObjectId, ComplexPojo>(getMongoDB(), ComplexPojo.class);

		pojongoComplex.removeAll();
		pojongoCategory.removeAll();

		pojongoComplex.insert(complexPojo);
		
		PojongoCursor<ComplexPojo> pojongoCursor = pojongoComplex.find();
		ComplexPojo complex = pojongoCursor.toList().get(0);
		assertEquals(category.getId(), complex.getCategory().getId());
	//	assertEquals(category.getId(), complex.getCategories().get(0).getId());
		
	}

	
	@Test
	public void shouldNotUseRef() throws Exception{
		Category category = new Category();
		category.setName("NewCategory");
		Pojongo<ObjectId, Category> pojongoCategory = new Pojongo<ObjectId, Category>(getMongoDB(), Category.class);
		pojongoCategory.insert(category);

		PojoWithListInnerObject pojo = new PojoWithListInnerObject();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		pojo.setCategories(categories);
		Pojongo<ObjectId, PojoWithListInnerObject> pojongoComplex = new Pojongo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);

		pojongoComplex.removeAll();
		pojongoCategory.removeAll();

		pojongoComplex.insert(pojo);
		
		PojongoCursor<PojoWithListInnerObject> pojongoCursor = pojongoComplex.find();
		PojoWithListInnerObject complex = pojongoCursor.toList().get(0);
		assertEquals(category.getId(), complex.getCategories().get(0).getId());
	}

	
	@Test
	public void shouldUpdateSimpleObject(){
		SubClassPojo pojo = new SubClassPojo();
		pojo.setAnIntegerField(44);
		pojo.setaLongField(44L);
		pojo.setaDoubleField(44.0);
		pojo.setStatus(Status.NEW);
		String extraInfo = "this extra info";
		pojo.setExtraProperty(extraInfo);
		
		Pojongo<ObjectId, SubClassPojo> pojongo = new Pojongo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo", new NullCommand<SubClassPojo>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(pojo);
		
		SimplePOJO simplePOJO = createSimplePojo();
		simplePOJO.setId(objectId);
		
		Pojongo<ObjectId, SimplePOJO> pojongo2 = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo", new NullCommand<SimplePOJO>());
		pojongo2.update(simplePOJO);

		SimplePOJO fixture = createSimplePojo();
		simplePOJO = pojongo.findOne(objectId);		
		assertTrue(fixture.getaDoubleField().equals(simplePOJO.getaDoubleField()));
		assertTrue(fixture.getAnIntegerField().equals(simplePOJO.getAnIntegerField()));
		assertTrue(fixture.getaLongField().equals(simplePOJO.getaLongField()));
		assertTrue(Status.NEW.equals(simplePOJO.getStatus()));
		
		
		
		SubClassPojo classPojo = pojongo.findOne(objectId);
		assertTrue(fixture.getaDoubleField().equals(classPojo.getaDoubleField()));
		assertTrue(fixture.getAnIntegerField().equals(classPojo.getAnIntegerField()));
		assertTrue(fixture.getaLongField().equals(classPojo.getaLongField()));
		assertTrue(Status.NEW.equals(classPojo.getStatus()));
		assertTrue(extraInfo.equals(classPojo.getExtraProperty()));
	}

	@Test
	public void shouldUpdateComplexObject(){
		SimplePOJO fixture = createSimplePojo();
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo", new NullCommand<SimplePOJO>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(fixture);

		Pojongo<ObjectId, SubClassPojo> pojongo2 = new Pojongo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo", new NullCommand<SubClassPojo>());
		SubClassPojo classPojo = pojongo2.findOne(objectId);
		compareTwoSimplePojos(fixture, classPojo);
	}

		
}
