package org.monjo.core.conversion;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.monjo.document.IdentifiableDocument;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class CollectionAnnotationTest {
	private static Mongo mongo;
	private static DB mongoDB;

	@BeforeClass
	public static void connectToMongo() throws Exception {
		mongo = new Mongo();
		mongoDB = mongo.getDB("annotationTest");
	}

	@Test
	public void shouldUseCollectionNameFromAnnotationIfGiven() throws Exception {
		mongoDB.getCollection("another_collection").drop();
		mongoDB.getCollection("another_collection");
		Monjo<ObjectId, SimplePojoAnnotated> monjo = new Monjo<ObjectId, SimplePojoAnnotated>(mongoDB, SimplePojoAnnotated.class);
		SimplePojoAnnotated annotated = new SimplePojoAnnotated();
		monjo.insert(annotated);
		DBCollection dbCollection = mongoDB.getCollection("another_collection");
		Assert.assertEquals(1, dbCollection.count());
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
