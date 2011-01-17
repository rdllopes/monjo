package org.pojongo.core.conversion;

import org.bson.types.ObjectId;
import org.hibernate.cfg.NamingStrategy;
import org.pojongo.document.IdentifiableDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultPojongoConverter implements PojongoConverter{
	private DefaultDocumentToObjectConverter defaultDocumentToObjectConverter = new DefaultDocumentToObjectConverter();
	private DefaultObjectToDocumentConverter defaultObjectToDocumentConverter = new DefaultObjectToDocumentConverter();
	
	
	public DefaultDocumentToObjectConverter from(DBObject document) {
		return defaultDocumentToObjectConverter.from(document);
	}
	public <T> T to(Class<T> objectType){
		return defaultDocumentToObjectConverter.to(objectType);
	}
	public ObjectToDocumentConverter from(Object javaObject) {
		return defaultObjectToDocumentConverter.from(javaObject);
	}
	public DBObject toDocument() {
		return defaultObjectToDocumentConverter.toDocument();
	}
	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		defaultDocumentToObjectConverter.setNamingStrategy(namingStrategy);
		defaultObjectToDocumentConverter.setNamingStrategy(namingStrategy);
	}
	@Override
	public DBObject getIdDocument(Object object) {
		if (object instanceof IdentifiableDocument) {
			@SuppressWarnings("rawtypes")
			IdentifiableDocument document = (IdentifiableDocument<ObjectId>) object;
			DBObject dbObject = new BasicDBObject();
			dbObject.put("_id", document.getId());
			return dbObject;
		}
		return null;
		
	}
	@Override
	public ObjectToDocumentConverter enableUpdate() {
		return defaultObjectToDocumentConverter.enableUpdate();
	}

	
}
