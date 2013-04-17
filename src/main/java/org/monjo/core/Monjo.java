package org.monjo.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.monjo.core.annotations.Entity;
import org.monjo.core.conversion.AnnotatedDocumentId;
import org.monjo.core.conversion.MonjoConverter;
import org.monjo.core.conversion.MonjoConverterFactory;
import org.monjo.document.IdentifiableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

/**
 * This is a main class of Monjo. It should be used to everyday works such as
 * find, save object, search by example.
 * 
 * 
 * @author Rodrigo di Lorenzo Lopes
 * 
 */
public class Monjo<DocumentId, T extends IdentifiableDocument<DocumentId>> {

	private static final Logger logger = LoggerFactory.getLogger(Monjo.class);
	private Class<T> clasz;
	private DBCollection collection;

	private Command<T> command;

	public Monjo(DB mongoDb, Class<T> clasz) {
		this(mongoDb, clasz, new NullCommand<T>());
	}

	public Monjo(DB mongoDb, Class<T> clasz, String collectionName) {
		this(mongoDb, clasz, collectionName, new NullCommand<T>());
	}

	public Monjo(DB mongoDb, Class<T> clasz, Command<T> command) {
		MonjoConverterFactory factory = MonjoConverterFactory.getInstance();
		String collectionName = findOutCollectionName(clasz, factory);
		initialize(mongoDb, clasz, collectionName, command);
	}

	protected String findOutCollectionName(Class<T> clasz, MonjoConverterFactory factory) {
		if (annotatedWithCollection(clasz)) {
			return clasz.getAnnotation(Entity.class).value();
		} else {
			return extractNameFromClassName(clasz, factory);
		}
	}

	private String extractNameFromClassName(Class<T> clasz, MonjoConverterFactory factory) {
		NamingStrategy namingStrategy = factory.getNamingStrategy();
		return namingStrategy.classToTableName(clasz.getName());
	}

	private boolean annotatedWithCollection(Class<T> clasz) {
		return clasz.isAnnotationPresent(Entity.class) && !"".equals(clasz.getAnnotation(Entity.class).value());
	}

	public Monjo(DB mongoDb, Class<T> clasz, String collectionName, Command<T> command) {
		initialize(mongoDb, clasz, collectionName, command);
	}

	public DBObject createCriteriaByExample(T example) {
		return getConverter().from(example).action(Operation.Search).toDocument();
	}

	public MonjoConverter<T> getConverter() {
		MonjoConverterFactory converterFactory = MonjoConverterFactory.getInstance();
		MonjoConverter<T> monjoConverter = converterFactory.getDefaultMonjoConverter(clasz);
		return monjoConverter;
	}

	/**
	 * Search all objects in mongo collection
	 * 
	 * @return a cursor to query result
	 */
	public MonjoCursor<T> find() {
		logger.debug("finding all items from collection:{}", collection.getName());
		DBCursor cursor = collection.find();
		return new MonjoCursor<T>(cursor, getConverter(), command);
	}

	/**
	 * Search object in mongo collection respecting a specified criteria
	 * 
	 * @param criteria
	 *            to be used in query
	 * @return a cursor to query result
	 */
	public MonjoCursor<T> findBy(DBObject criteria) {
		logger.debug("finding all items from collection:{} by criteria:{}", collection.getName(), criteria);
		DBCursor cursor = collection.find(criteria);
		return new MonjoCursor<T>(cursor, getConverter(), command);
	}

	public MonjoCursor<T> findByExample(T example) {
		return findBy(createCriteriaByExample(example));
	}

	/**
	 * Find Object by Id. It presumes that id field is filled.
	 * 
	 * @param <C>
	 *            an IdentifiableDocument type
	 * @param collection
	 *            that contains the document to be found
	 * @param c
	 *            Object example used to get id and class to target document
	 * @return document converted to object from collection that matches _id ==
	 *         c.getId()
	 * @throws Exception
	 */
	public T findOne(T c) {
		return findOne((DocumentId)AnnotatedDocumentId.get(c));
	}

	/**
	 * Classic search by Id.
	 * 
	 * @param <C>
	 * @param collection
	 *            that contains the document to be found
	 * @param id
	 *            of document to be found (if that exists)
	 * @param clasz
	 * @return object if found an object that matches with criteria used
	 * @throws Exception
	 */
	public T findOne(DocumentId id) {
		BasicDBObject criteria = new BasicDBObject("_id", id);
		logger.debug("finding an item from collection:{} by criteria:{}", collection.getName(), criteria);
		DBObject dbObject = collection.findOne(criteria);
		try {
			logger.debug("item found:{}", dbObject);
			if (dbObject == null)
				return null;
			return command.execute(getConverter().from(dbObject).to());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the count of documents in the collection
	 * 
	 * @return long
	 */
	public long getCount() {
		return collection.getCount();
	}

	private void initialize(DB mongoDb, Class<T> clasz, String collectionName, Command<T> command2) {
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
	public DocumentId insert(T identifiableDocument) {
		DBObject dbObject = getConverter().from(identifiableDocument).toDocument();
		logger.info("inserting an item:{} in collection:{}", dbObject, collection.getName());
		collection.insert(dbObject);
		AnnotatedDocumentId.set(identifiableDocument, (DocumentId) dbObject.get("_id"));
		return (DocumentId) dbObject.get("_id");
	}

	/**
	 * Remove all items from collection
	 */
	public void removeAll() {
		// carai mano, o que essa cara ta fazendo !?
		logger.warn("dropping collection {}", collection.getName());
		collection.drop();
	}

	/**
	 * Remove an item with specific id
	 * 
	 * @param id
	 */
	public void removeBy(DocumentId id) {
		BasicDBObject basicObject = new BasicDBObject("_id", id);
		logger.warn("removing item:{} from collection:{}", basicObject, collection.getName());
		removeByCriteria(basicObject);
	}

	/**
	 * Remove one or more items based on the criteria
	 * 
	 * @param criteria
	 */
	public void removeByCriteria(DBObject criteria) {
		logger.warn("removing item(s):{} from collection:{}", criteria, collection.getName());
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
	public DocumentId save(T identifiableDocument) {
		DBObject dbObject = getConverter().from(identifiableDocument).toDocument();
		logger.info("inserting an item:{} in collection:{}", dbObject, collection.getName());
		collection.save(dbObject);
		AnnotatedDocumentId.set(identifiableDocument, (DocumentId) dbObject.get("_id"));
		return (DocumentId) dbObject.get("_id");
	}

	public DocumentId update(T identifiableDocument) {
		DBObject dbObject = createUpdateCriteria(identifiableDocument);
		DBObject dbObject2 = getConverter().getIdDocument(identifiableDocument);
		update(dbObject2, dbObject);
		return (DocumentId) AnnotatedDocumentId.get(identifiableDocument);
	}

	public DocumentId updateWithAddSet(T identifiableDocument) {
		DBObject dbObject = createUpdateWithAddSetCriteria(identifiableDocument);
		DBObject dbObject2 = getConverter().getIdDocument(identifiableDocument);
		update(dbObject2, dbObject);
		return (DocumentId) AnnotatedDocumentId.get(identifiableDocument);
	}

	public DBObject createUpdateCriteria(T identifiableDocument) {
		return getConverter().from(identifiableDocument).action(Operation.Update).toDocument();
	}

	public DBObject createUpdateWithAddSetCriteria(T identifiableDocument) {
		return getConverter().from(identifiableDocument).action(Operation.UpdateWithAddSet).toDocument();
	}

	public void update(DBObject query, DBObject update) {
		logger.info("updating an item:{} for {} in collection:{}", new Object[] { query, update, collection.getName() });
		collection.update(query, update, true, false);
	}

	/**
	 * 
	 * @param fieldname
	 *            Special field use to selection
	 * @param identifiableDocument
	 *            to be updated
	 * @return id of identifiableDocument
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public DocumentId updateInnerObject(T identifiableDocument, String fieldname) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (AnnotatedDocumentId.get(identifiableDocument) != null) {
			throw new IllegalAccessException("Id already filled");
		}
		List list = (List) PropertyUtils.getProperty(identifiableDocument, fieldname);
		Object innerObject = list.get(0);
		DBObject dbObject = getConverter().from(identifiableDocument).action(Operation.UpdateInnerObject).toDocument();
		DBObject dbObject2 = ((MonjoConverter<T>) getConverter().setPrefix(fieldname)).getIdDocument(innerObject);
		logger.info("updating an item:{} for {} in collection:{}", new Object[] { dbObject2, dbObject, collection.getName() });
		collection.update(dbObject2, dbObject, true, false);
		return (DocumentId) AnnotatedDocumentId.get(identifiableDocument);

	}

}
