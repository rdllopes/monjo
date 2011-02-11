package org.monjo.core.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.monjo.test.util.HamcrestPatch.classEqualTo;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.monj.example.AnotherPojo;
import org.monj.example.Category;
import org.monj.example.ComplexPojo;
import org.monj.example.PojoWithListInnerObject;
import org.monj.example.SimplePOJO;
import org.monj.example.Status;
import org.monj.example.StatusConverter;
import org.monj.example.SubClassPojo;
import org.monj.example.User;
import org.monjo.test.util.MongoDBTest;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;


public class MonjoTest extends MongoDBTest{

	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance().configure(new DefaultNamingStrategy());
		ConvertUtils.register(new StatusConverter(), Status.class);				
	}
	
	@Test
	public void deveriaGravarElemento(){
		SimplePOJO pojo = createSimplePojo();
		
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);
		
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
	public void deveriaEncontrarDocumentoInserido(){
		SimplePOJO pojo = createSimplePojo();

		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
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
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		
		assertEquals(5, list.size());
	}

	private void insertPojoCollection(Monjo<ObjectId, SimplePOJO> pojongo) {
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
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		List<SimplePOJO> list2 = pojongo.find().skip(4).limit(1).toList();
			
		assertEquals(list.get(4), list2.get(0));
	}
	
	@Test
	public void deveriaContarDocumentosColecao(){
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		insertPojoCollection(pojongo);
			
		assertEquals(30, pojongo.getCount());
	}
	
	@Test
	public void deveriaFiltrarUsandoIn() throws Exception{
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
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
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
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
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.insert(simplePOJO);
		SimplePOJO simplePOJO2 = pojongo.findOne(simplePOJO.getId());
		assertEquals(simplePOJO.getStatus(),simplePOJO2.getStatus());
		
	}
	
	
	@Test
	public void shouldNotUseRef2() throws Exception{
		User user = new User();
		user.setName("NewCategory");

		AnotherPojo anotherPojo = createAnotherPojo(user);
		
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

		ComplexPojo complexPojo = createComplexPojo(category);
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

	private ComplexPojo createComplexPojo(Category category) {
		ComplexPojo complexPojo = new ComplexPojo();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		complexPojo.setCategory(category);
		complexPojo.setDescription("pojo complexo");
		return complexPojo;
	}


	private AnotherPojo createAnotherPojo(User category) {
		AnotherPojo another = new AnotherPojo();
		another.setUser(category);
		another.setDescription("pojo complexo");
		return another;
	}

	
	@Test
	public void shouldNotUseRef() throws Exception{
		PojoWithListInnerObject pojo = createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongoComplex = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);

		pojongoComplex.removeAll();

		pojongoComplex.insert(pojo);
		
		MonjoCursor<PojoWithListInnerObject> pojongoCursor = pojongoComplex.find();
		PojoWithListInnerObject complex = pojongoCursor.toList().get(0);
		assertNotNull(complex.getCategories().get(0).getId());
	}

	private PojoWithListInnerObject createMegaZordePojo() {
		Category category = new Category();
		category.setName("NewCategory");

		PojoWithListInnerObject pojo = new PojoWithListInnerObject();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		pojo.setCategories(categories);
		return pojo;
	}

	
	@Test
	public void shouldUpdateSimpleObject(){
		Status thing = Status.Delta;
		System.out.println(thing.getClass().isEnum());
		
		SubClassPojo pojo = new SubClassPojo();
		pojo.setAnIntegerField(44);
		pojo.setaLongField(44L);
		pojo.setaDoubleField(44.0);
		pojo.setStatus(thing);
		String extraInfo = "this extra info";
		pojo.setExtraProperty(extraInfo);
		
		Monjo<ObjectId, SubClassPojo> pojongo = new Monjo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo", new NullCommand<SubClassPojo>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(pojo);
		
		SimplePOJO simplePOJO = createSimplePojo();
		simplePOJO.setId(objectId);
		
		Monjo<ObjectId, SimplePOJO> pojongo2 = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo", new NullCommand<SimplePOJO>());
		pojongo2.update(simplePOJO);

		SimplePOJO fixture = createSimplePojo();
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
	public void shouldUpdateComplexObject(){
		SimplePOJO fixture = createSimplePojo();
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class, "simplePojo", new NullCommand<SimplePOJO>());
		pojongo.removeAll();
		ObjectId objectId = pojongo.insert(fixture);

		Monjo<ObjectId, SubClassPojo> pojongo2 = new Monjo<ObjectId, SubClassPojo>(getMongoDB(), SubClassPojo.class, "simplePojo", new NullCommand<SubClassPojo>());
		SubClassPojo classPojo = pojongo2.findOne(objectId);
		compareTwoSimplePojos(fixture, classPojo);
	}

	@Test
	public void shouldFindByExample() {
		PojoWithListInnerObject createMegaZordePojo = createMegaZordePojo();
		Monjo<ObjectId, PojoWithListInnerObject> pojongo = new Monjo<ObjectId, PojoWithListInnerObject>(getMongoDB(), PojoWithListInnerObject.class);
		pojongo.removeAll();
		pojongo.insert(createMegaZordePojo);
		createMegaZordePojo.setId(null);
		PojoWithListInnerObject result = pojongo.findByExample(createMegaZordePojo).toList().get(0);
		assertNotNull(result.getCategories().get(0).getId());		
	}

	@Test
	public void shouldFindByExampleSoSo() {
		PojoWithListInnerObject createMegaZordePojo = createMegaZordePojo();
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
	
	@Test
	public void shouldFindBySimpleExample() {
		SimplePOJO simplePOJO = createSimplePojo();
		Monjo<ObjectId, SimplePOJO> pojongo = new Monjo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.removeAll();
		pojongo.insert(simplePOJO);
		SimplePOJO result = pojongo.findByExample(simplePOJO).toList().get(0);
		assertNotNull(result.getId());		
	}

		
}
