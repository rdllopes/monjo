package org.pojongo.core.conversion;

import org.hibernate.cfg.NamingStrategy;
import org.pojongo.document.IdentifiableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This is a main class of Pojongo. It should be used to everyday works such as 
 * find, save object, search by example.
 * 
 * 
 * @author Rodrigo di Lorenzo Lopes
 * 
 */
public class Pojongo<T, C extends IdentifiableDocument<T>> {
	
	private static final Logger logger = LoggerFactory.getLogger(Pojongo.class);
	private Class<C> clasz;
	private DBCollection collection;
	
	public Pojongo(DB mongoDb, Class<C> clasz, String collectionName, Command<C> command) {
		initialize(mongoDb, clasz, collectionName, command);
	}
	
	public Pojongo(DB mongoDb, Class<C> clasz, Command<C> command) {
		PojongoConverterFactory factory = PojongoConverterFactory.getInstance();
		NamingStrategy namingStrategy = factory.getNamingStrategy();
		String collectionName = namingStrategy.classToTableName(clasz.getName());				
		initialize(mongoDb, clasz, collectionName, command);
		
	}

	public Pojongo(DB mongoDb, Class<C> clasz) {
		this(mongoDb, clasz, new NullCommand<C>());
	}

	private void initialize(DB mongoDb, Class<C> clasz, String collectionName, Command<C> command2) {
		collection = mongoDb.getCollection(collectionName);
		converter = PojongoConverterFactory.getInstance().getDefaultPojongoConverter();
		this.clasz = clasz;
		this.command = command2;
	}
	
	private PojongoConverter converter;
	private Command<C> command;

	/**
	 * 
	 The save() command in the mongo shell provides a shorthand syntax to
	 * perform a single object update with upsert:
	 * 
	 * <pre>
	 * // x is some JSON style object
	 * db.mycollection.save(x); // updates if exists; inserts if new
	 * </pre>
	 * 
	 * save() does an upsert if x has an _id field and an insert if it does not.
	 * Thus, normally, you will not need to explicitly request upserts, just use
	 * save().
	 * 
	 * @param collection
	 * @param identifiableDocument
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T save(C identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		logger.debug("inserting an item:{} in collection:{}", dbObject, collection.getName());		
		collection.save(dbObject);
		identifiableDocument.setId((T) dbObject.get("_id"));
		return (T) dbObject.get("_id");
	}
	
	
	@SuppressWarnings("unchecked")
	public T update(C identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).enableUpdate().toDocument();
		DBObject dbObject2 = converter.getIdDocument(identifiableDocument);
		logger.debug("updating an item:{} for {} in collection:{}", new Object[] {dbObject2, dbObject, collection.getName()});		
		collection.update(dbObject2, dbObject, true, false);
		identifiableDocument.setId((T) dbObject.get("_id"));
		return (T) dbObject.get("_id");
	}
	
	/**
	 *
	 * Insert object without lookup verification
	 *   
	 * @param collection
	 * @param identifiableDocument
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public T insert(C identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		logger.debug("inserting an item:{} in collection:{}", dbObject, collection.getName());
		collection.insert(dbObject);
		identifiableDocument.setId((T) dbObject.get("_id"));
		return (T) dbObject.get("_id");
	}

	/**
	 * Classic search by Id.
	 * 
	 * @param <C>
	 * @param collection that contains the document to be found
	 * @param id of document to be found (if that exists)
	 * @param clasz
	 * @return
	 * @throws Exception 
	 */
	public C findOne(T id){
		BasicDBObject criteria = new BasicDBObject("_id", id);
		logger.debug("finding an item from collection:{} by criteria:{}", collection.getName(), criteria);
		DBObject dbObject = collection.findOne(criteria);
		try {
			logger.debug("item found:{}", dbObject);
			return command.execute(converter.from(dbObject).to(clasz));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Find Object by Id. Its presume that id field is filled.
	 * 
	 * @param <C> an IdentifiableDocument type
	 * @param collection that contains the document to be found
	 * @param c Object example used to get id and class to target document 
	 * @return document converted to object from collection that matches _id == c.getId()  
	 * @throws Exception 
	 */
	public C findOne(C c) {
		return findOne(c.getId());
	}

	/**
	 * Search object in mongo collection respecting a specified criteria
	 * @param criteria to be used in query
	 * @return a cursor to query result 
	 */
	public PojongoCursor<C> findBy(DBObject criteria){
		logger.debug("finding all items from collection:{} by criteria:{}", collection.getName(), criteria);
		DBCursor cursor = collection.find(criteria);
		return new PojongoCursor<C>(cursor, converter, clasz, command);
	}

	/**
	 * Search all objects in mongo collection 
	 * @return a cursor to query result
	 */
	public PojongoCursor<C> find(){
		logger.debug("finding all items from collection:{}", collection.getName());
		DBCursor cursor = collection.find();
		return new PojongoCursor<C>(cursor, converter, clasz, command);
	}
	
	/**
	 * Remove one or more items based on the criteria
	 * @param criteria
	 */
	public void removeByCriteria(DBObject criteria){
		logger.debug("removing item(s):{} from collection:{}", criteria, collection.getName());
		collection.remove(criteria);
	}

	/**
	 * Remove an item with specific id
	 * @param id
	 */
	public void removeBy(T id){
		BasicDBObject basicObject = new BasicDBObject("_id", id);
		logger.debug("removing item:{} from collection:{}", basicObject, collection.getName());
		removeByCriteria(basicObject);
	}
	
	/**
	 * Remove all items from collection
	 */
	public void removeAll(){
		logger.debug("dropping collection {}", collection.getName());
		collection.drop();
	}
	
	/**
	 * Return the count of documents in the collection
	 * @return long
	 */
	public long getCount(){
		return collection.getCount();
	}
	
}
