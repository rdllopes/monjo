package org.pojongo.core.conversion;

import java.util.Set;

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
	private ObjectToDocumentConverter converter = ObjectToDocumentConverterFactory.getInstance().getDefaultDocumentConverter();	
	
	public T save(DBCollection collection, IdentifiableDocument<T> identifiableDocument){
		DBObject dbObject = converter.from(identifiableDocument).toDocument();
		collection.save(dbObject);
		return (T) dbObject.get("_id");
	}

	public T verifyAndsave(DBCollection collection, IdentifiableDocument<T> identifiableDocument){
		DBObject document = new BasicDBObject();
		if (identifiableDocument.getId() != null){
			document.put("_id", identifiableDocument.getId());			
		}
		DBObject oldCopy = collection.findOne(document);
		DBObject newCopy = converter.from(identifiableDocument).toDocument();
		DBObject merge = merge(oldCopy, newCopy);
		collection.save(merge);
		return (T) merge.get("_id");
	}

	private DBObject merge(DBObject oldCopy, DBObject newCopy) {
		if (oldCopy == null) return newCopy;
		DBObject object = new BasicDBObject();
		Set<String> oldKeySet = oldCopy.keySet();
		Set<String> newKeySet = newCopy.keySet();
		
		for (String key : oldKeySet) {
			Object newValue = newCopy.get(key);
			object.put(key, newValue);
			newKeySet.remove(key);
		}
		
		for (String key : newKeySet) {
			object.put(key, newCopy.get(key));
		}		
		return object;
	}

}
