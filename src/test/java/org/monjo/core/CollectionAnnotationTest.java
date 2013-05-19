package org.monjo.core;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.monjo.core.annotations.Entity;
import org.monjo.core.conversion.MonjoConverterFactory;

import com.mongodb.DB;

public class CollectionAnnotationTest {

	@Test
	public void shouldUseCollectionNameFromAnnotationIfGiven() throws Exception {
		Monjo<ObjectId, SimplePojoAnnotated> monjo = 
				new Monjo<ObjectId, CollectionAnnotationTest.SimplePojoAnnotated>(Mockito.mock(DB.class), SimplePojoAnnotated.class);
		String string = monjo.findOutCollectionName(SimplePojoAnnotated.class, MonjoConverterFactory.getInstance());
		Assert.assertEquals("another_collection", string);
	}
	

	@Entity("another_collection")
	public static class SimplePojoAnnotated {
		
	}

}
