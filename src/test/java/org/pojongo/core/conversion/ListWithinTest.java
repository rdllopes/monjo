package org.pojongo.core.conversion;

import java.util.List;

import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pojongo.example.ListWithin;
import org.pojongo.test.util.MongoDBTest;

public class ListWithinTest extends MongoDBTest{
	
	@Before
	public void setUp() throws Exception {
		PojongoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultObjectConverter();
	}

	@Test
	public void deveriaGravarElementoComList(){
		ListWithin pojo = new ListWithin();
		pojo.addItem(42);
		pojo.addItem(43);
		
		Pojongo<ObjectId, ListWithin> pojongo = new Pojongo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = pojongo.save(pojo);
		ListWithin listWithin = pojongo.findOne(objectId);
		List<String> strings = listWithin.getNames();
		Assert.assertTrue(strings.contains("42"));
		Assert.assertTrue(strings.contains("43"));
	}
	

}
