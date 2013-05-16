package org.monjo.test.util;

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

	private static DBCollection monjoCollection;
	private static Mongo mongo;
	private static DB mongoDB;

	@BeforeClass
	public static void connectToMongo() throws Exception {
		mongo = new Mongo();		
		mongoDB = mongo.getDB("monjoTest");
		monjoCollection = mongoDB.getCollection("SimplePOJO");
	}

	
	public static DB getMongoDB() {
		return mongoDB;
	}
	@AfterClass
	public static void dispose() {
		mongo.close();
	}

	@After
	public void tearDown() {
		getMonjoCollection().remove(new BasicDBObject());
	}
	
	protected void saveToMongo(DBObject dbObject) {
		getMonjoCollection().save(dbObject);
	}
	
	protected DBObject getFromMongo(Object id) {
		return getMonjoCollection().findOne(QueryBuilder.start("_id").is(id).get());
	}


	public static DBCollection getMonjoCollection() {
		return monjoCollection;
	}

}
