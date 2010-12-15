package org.pojongo.core.conversion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.example.Category;
import org.pojongo.example.ComplexPojo;
import org.pojongo.example.SimplePOJO;
import org.pojongo.example.SimplePOJO.Status;
import org.pojongo.example.StatusConverter;
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
	}
	
	@Test
	public void deveriaGravarElemento(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);
		
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));
		
		Class anIntegerFieldClass = document.get("anIntegerField").getClass();
		assertThat(anIntegerFieldClass, is(equalTo(Integer.class)));
		assertThat((Integer) document.get("anIntegerField"), is(equalTo(42)));

		Class aLongFieldClass = document.get("aLongField").getClass();
		assertThat(aLongFieldClass, is(equalTo(Long.class)));
		assertThat((Long) document.get("aLongField"), is(equalTo(43L)));

		Class aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, is(equalTo(Double.class)));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(44.0)));
	}

	@Test
	public void deveriaAtualizarElemento(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.save(pojo);

		pojo = new SimplePOJO();
		pojo.setaDoubleField(45.0);
		pojo.setId(objectId);
		
		pojongo.save(pojo);
		DBObject document = getPojongoCollection().findOne(new BasicDBObject("_id", objectId));		

		assertNull (document.get("anIntegerField"));
		assertNull (document.get("aLongField"));

		Class aDoubleFieldClass = document.get("aDoubleField").getClass();
		assertThat(aDoubleFieldClass, is(equalTo(Double.class)));
		assertThat((Double) document.get("aDoubleField"), is(equalTo(45.0)));

	}

	@Test
	public void deveriaEncontrarDocumentoInserido(){
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);

		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		ObjectId objectId = pojongo.insert(pojo);
		
		SimplePOJO simplePOJO = pojongo.findOne(new SimplePOJO(objectId));
		assertThat(pojo.getAnIntegerField(), is(simplePOJO.getAnIntegerField()));
		assertThat(pojo.getaLongField(), is(simplePOJO.getaLongField()));
		assertThat(pojo.getaDoubleField(), is(simplePOJO.getaDoubleField()));

	}
	
	@Test
	public void deveriaLimitarResultados() throws Exception{
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		for(int i = 0; i < 30; i++){
			SimplePOJO pojo = new SimplePOJO();
			pojo.setAnIntegerField(i);
			pojo.setaLongField(43L);
			pojo.setaDoubleField(44.0);
			
			pojongo.insert(pojo);
		}
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		
		assertEquals(5, list.size());
	}
	
	@Test
	public void deveriaComecarNoQuintoResultado() throws Exception{
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		for(int i = 0; i < 30; i++){
			SimplePOJO pojo = new SimplePOJO();
			pojo.setAnIntegerField(i);
			pojo.setaLongField(43L);
			pojo.setaDoubleField(44.0);
			
			pojongo.insert(pojo);
		}
		
		List<SimplePOJO> list = pojongo.find().limit(5).toList();
		List<SimplePOJO> list2 = pojongo.find().skip(4).limit(1).toList();
			
		assertEquals(list.get(4), list2.get(0));
	}
	
	@Test
	public void deveriaContarDocumentosColecao(){
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		
		for(int i = 0; i < 30; i++){
			SimplePOJO pojo = new SimplePOJO();
			pojo.setAnIntegerField(i);
			pojo.setaLongField(43L);
			pojo.setaDoubleField(44.0);
			
			pojongo.insert(pojo);
		}
			
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
	public void testEnumTypes() throws Exception {
		SimplePOJO simplePOJO = new SimplePOJO();
		simplePOJO.setStatus(Status.NEW);
		Pojongo<ObjectId, SimplePOJO> pojongo = new Pojongo<ObjectId, SimplePOJO>(getMongoDB(), SimplePOJO.class);
		pojongo.insert(simplePOJO);
		ConvertUtils.register(new StatusConverter(), Status.class);
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
		complexPojo.setCategory(category);
		complexPojo.setDescription("pojo complexo");
		Pojongo<ObjectId, ComplexPojo> pojongoComplex = new Pojongo<ObjectId, ComplexPojo>(getMongoDB(), ComplexPojo.class);

		pojongoComplex.removeAll();
		pojongoCategory.removeAll();

		pojongoComplex.insert(complexPojo);
		
		PojongoCursor<ComplexPojo> pojongoCursor = pojongoComplex.find();
		assertEquals(category.getId(), pojongoCursor.toList().get(0).getCategory().getId());
		
	}

	
}
