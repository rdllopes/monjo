package org.monjo.core.conversion;

import java.util.List;

import org.bson.types.ObjectId;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.monjo.core.conversion.Monjo;
import org.monjo.core.conversion.MonjoConverterFactory;
import org.monjo.example.ListWithin;
import org.monjo.test.util.MongoDBTest;

public class ListWithinTest extends MongoDBTest{
	
	@Before
	public void setUp() throws Exception {
		MonjoConverterFactory.getInstance()
				.configure(new DefaultNamingStrategy())
				.getDefaultObjectConverter(ListWithin.class);
	}

	@Test
	public void deveriaGravarElementoComList(){
		ListWithin pojo = new ListWithin();
		pojo.addItem(42);
		pojo.addItem(43);
		
		Monjo<ObjectId, ListWithin> pojongo = new Monjo<ObjectId, ListWithin>(getMongoDB(), ListWithin.class);
		ObjectId objectId = pojongo.save(pojo);
		ListWithin listWithin = pojongo.findOne(objectId);
		List<String> strings = listWithin.getNames();
		Assert.assertTrue(strings.contains("42"));
		Assert.assertTrue(strings.contains("43"));
	}
	

}
