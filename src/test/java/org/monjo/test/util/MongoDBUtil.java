package org.monjo.test.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.QueryBuilder;

public class MongoDBUtil {

	private static DBCollection monjoCollection;
	private static Mongo mongo;
	private static DB mongoDB;

	public static DBCollection getMonjoCollection() {
		if (monjoCollection == null){
			connectToMongo();
		}
		return monjoCollection;
	}

	public static DB getMongoDB() {
		if (mongoDB == null){
			connectToMongo();
		}
		return mongoDB;
	}
	
	//@BeforeClass
	public static void connectToMongo() {
		try {
			mongo = new Mongo();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
		mongoDB = mongo.getDB("monjoTest");
		monjoCollection = mongoDB.getCollection("SimplePOJO");
	}

	
	// @AfterClass
	public static void dispose() {
		mongo.close();
	}

	public static void tearDown() {
		getMonjoCollection().remove(new BasicDBObject());
	}
	
	public static void saveToMongo(DBObject dbObject) {
		getMonjoCollection().save(dbObject);
	}
	
	public static DBObject getFromMongo(Object id) {
		return getMonjoCollection().findOne(QueryBuilder.start("_id").is(id).get());
	}

}
