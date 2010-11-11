package org.pojongo.core.conversion;

import java.util.Set;

import org.pojongo.document.IdentifiableDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * This is a main class of Pojongo. It should be used to everyday works such as 
 * find, save object, search by example.
 * 
 * 
 * @author Rodrigo di Lorenzo Lopes
 * 
 */
public class Pojongo<T> {
	private PojongoConverter converter = PojongoConverterFactory.getInstance().getDefaultPojongoConverter();

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
	public T save(DBCollection collection,
			IdentifiableDocument<T> identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		collection.save(dbObject);
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
	public T insert(DBCollection collection, IdentifiableDocument<T> identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		collection.insert(dbObject);
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
	 */
	public <C extends IdentifiableDocument<T>> C findOne(DBCollection collection, T id, Class<C> clasz){
		DBObject dbObject = collection.findOne(new BasicDBObject("_id", id));
		return converter.from(dbObject).to(clasz);
	}

	/**
	 * Find Object by Id. Its presume that id field is filled.
	 * 
	 * @param <C> an IdentifiableDocument type
	 * @param collection that contains the document to be found
	 * @param c Object example used to get id and class to target document 
	 * @return document converted to object from collection that matches _id == c.getId()  
	 */
	@SuppressWarnings("unchecked")
	public <C extends IdentifiableDocument<T>> C findOne(DBCollection collection, C c){
		return (C) findOne(collection, c.getId(), c.getClass());
	}

}
