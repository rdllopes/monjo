package org.monjo.core.conversion;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.monjo.document.IdentifiableDocument;

import com.mongodb.DB;

public class CollectionAnnotationTest {

	@Test
	public void shouldUseCollectionNameFromAnnotationIfGiven() throws Exception {
		Monjo<ObjectId, SimplePojoAnnotated> monjo = 
				new Monjo<ObjectId, CollectionAnnotationTest.SimplePojoAnnotated>(Mockito.mock(DB.class), SimplePojoAnnotated.class);
		String string = monjo.findOutCollectionName(SimplePojoAnnotated.class, MonjoConverterFactory.getInstance());
		Assert.assertEquals("another_collection", string);
	}
	

	@Collection("another_collection")
	public static class SimplePojoAnnotated implements IdentifiableDocument<ObjectId>{
		private ObjectId objectId;

		@Override
		public ObjectId getId() {
			return objectId;
		}

		@Override
		public void setId(ObjectId id) {
			this.objectId = id;
		}
		
	}

}
