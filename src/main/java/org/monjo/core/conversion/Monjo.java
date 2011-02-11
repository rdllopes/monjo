package org.monjo.core.conversion;

import org.hibernate.cfg.NamingStrategy;
import org.monjo.document.IdentifiableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This is a main class of Monjo. It should be used to everyday works such as 
 * find, save object, search by example.
 * 
 * 
 * @author Rodrigo di Lorenzo Lopes
 * 
 */
public class Monjo<T, C extends IdentifiableDocument<T>> {
	
	private static final Logger logger = LoggerFactory.getLogger(Monjo.class);
	private Class<C> clasz;
	private DBCollection collection;
	
	private Command<C> command;
	
	public Monjo(DB mongoDb, Class<C> clasz) {
		this(mongoDb, clasz, new NullCommand<C>());
	}

	public Monjo(DB mongoDb, Class<C> clasz, Command<C> command) {
		MonjoConverterFactory factory = MonjoConverterFactory.getInstance();
		String collectionName = findOutCollectionName(clasz, factory);
		initialize(mongoDb, clasz, collectionName, command);
	}

	protected String findOutCollectionName(Class<C> clasz, MonjoConverterFactory factory) {
		if (annotatedWithCollection(clasz)) {
			return clasz.getAnnotation(Collection.class).value();
		} else {
			return extractNameFromClassName(clasz, factory);							
		}
	}

	private String extractNameFromClassName(Class<C> clasz, MonjoConverterFactory factory) {
		NamingStrategy namingStrategy = factory.getNamingStrategy();
		return namingStrategy.classToTableName(clasz.getName());
	}

	private boolean annotatedWithCollection(Class<C> clasz) {
		return clasz.isAnnotationPresent(Collection.class);
	}
	
	public Monjo(DB mongoDb, Class<C> clasz, String collectionName, Command<C> command) {
		initialize(mongoDb, clasz, collectionName, command);
	}
	public DBObject createCriteriaByExample(C example) {
		return getConverter().from(example).enableSearch().toDocument();
	}

	public MonjoConverter<C> getConverter() {
		 MonjoConverterFactory converterFactory = MonjoConverterFactory.getInstance();
		 MonjoConverter<C> monjoConverter = converterFactory.getDefaultPojongoConverter(clasz);
		 return monjoConverter;
	}

	/**
	 * Search all objects in mongo collection 
	 * @return a cursor to query result
	 */
	public MonjoCursor<C> find(){
		logger.debug("finding all items from collection:{}", collection.getName());
		DBCursor cursor = collection.find();
		return new MonjoCursor<C>(cursor, getConverter(), command);
	}
	
	
	/**
	 * Search object in mongo collection respecting a specified criteria
	 * @param criteria to be used in query
	 * @return a cursor to query result 
	 */
	public MonjoCursor<C> findBy(DBObject criteria){
		logger.debug("finding all items from collection:{} by criteria:{}", collection.getName(), criteria);
		DBCursor cursor = collection.find(criteria);
		return new MonjoCursor<C>(cursor, getConverter(), command);
	}
	
	public MonjoCursor<C> findByExample(C example) {
		return findBy(createCriteriaByExample(example));
	}

	/**
	 * Find Object by Id. It presumes that id field is filled.
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
	 * Classic search by Id.
	 * 
	 * @param <C>
	 * @param collection that contains the document to be found
	 * @param id of document to be found (if that exists)
	 * @param clasz
	 * @return object if found an object that matches with criteria used
	 * @throws Exception 
	 */
	public C findOne(T id){
		BasicDBObject criteria = new BasicDBObject("_id", id);
		logger.debug("finding an item from collection:{} by criteria:{}", collection.getName(), criteria);
		DBObject dbObject = collection.findOne(criteria);
		try {
			logger.debug("item found:{}", dbObject);
			if (dbObject == null) return null;
			return command.execute(getConverter().from(dbObject).to());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the count of documents in the collection
	 * @return long
	 */
	public long getCount(){
		return collection.getCount();
	}

	private void initialize(DB mongoDb, Class<C> clasz, String collectionName, Command<C> command2) {
		collection = mongoDb.getCollection(collectionName);
		this.clasz = clasz;
		this.command = command2;
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
		DBObject dbObject = getConverter().from(identifiableDocument).toDocument();
		logger.debug("inserting an item:{} in collection:{}", dbObject, collection.getName());
		collection.insert(dbObject);
		identifiableDocument.setId((T) dbObject.get("_id"));
		return (T) dbObject.get("_id");
	}

	/**
	 * Remove all items from collection
	 */
	public void removeAll(){
		logger.debug("dropping collection {}", collection.getName());
		collection.drop();
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
	 * Remove one or more items based on the criteria
	 * @param criteria
	 */
	public void removeByCriteria(DBObject criteria){
		logger.debug("removing item(s):{} from collection:{}", criteria, collection.getName());
		collection.remove(criteria);
	}
	
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
		DBObject dbObject = getConverter().from(identifiableDocument).toDocument();
		logger.debug("inserting an item:{} in collection:{}", dbObject, collection.getName());
		collection.save(dbObject);
		identifiableDocument.setId((T) dbObject.get("_id"));
		return (T) dbObject.get("_id");
	}

	public T update(C identifiableDocument) {
		DBObject dbObject = getConverter().from(identifiableDocument).enableUpdate().toDocument();
		DBObject dbObject2 = getConverter().getIdDocument(identifiableDocument);
		logger.debug("updating an item:{} for {} in collection:{}", new Object[] {dbObject2, dbObject, collection.getName()});		
		collection.update(dbObject2, dbObject, true, false);
		return identifiableDocument.getId();
	}
	
}
