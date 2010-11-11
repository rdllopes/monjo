package org.pojongo.core.conversion;

import org.pojongo.document.IdentifiableDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
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
	
	@SuppressWarnings("unchecked")
	public T insert(DBCollection collection, IdentifiableDocument<T> identifiableDocument) {
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		collection.insert(dbObject);
		return (T) dbObject.get("_id");
	}
	
	public <C extends IdentifiableDocument<T>> C findOne(DBCollection collection, T t, Class<C> clasz){
		DBObject dbObject = collection.findOne(new BasicDBObject("_id", t));
		return converter.from(dbObject).to(clasz);
	}

	@SuppressWarnings("unchecked")
	public <C extends IdentifiableDocument<T>> C findOne(DBCollection collection, C c){
		return (C) findOne(collection, c.getId(), c.getClass());
	}

}
