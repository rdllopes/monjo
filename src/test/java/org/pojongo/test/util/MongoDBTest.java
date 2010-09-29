package org.pojongo.test.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class MongoDBTest {

	private static DBCollection pojongoCollection;
	private static Mongo mongo;

	@BeforeClass
	public static void connectToMongo() throws Exception {
		mongo = new Mongo();
		
		DB pojongoDB = mongo.getDB("pojongoTest");
		pojongoCollection = pojongoDB.getCollection("Pojongo");
	}

	@AfterClass
	public static void dispose() {
		mongo.close();
	}
	
	@After
	public void tearDown() {
		pojongoCollection.remove(new BasicDBObject());
	}
	
	protected void saveToMongo(DBObject dbObject) {
		pojongoCollection.save(dbObject);
	}
	
	protected DBObject getFromMongo(Object id) {
		return pojongoCollection.findOne(QueryBuilder.start("_id").is(id).get());
	}

}
