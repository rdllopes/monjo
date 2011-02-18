package org.monjo.core.conversion;

import org.bson.types.ObjectId;
import org.hibernate.cfg.NamingStrategy;
import org.monjo.document.IdentifiableDocument;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DefaultPojongoConverter<T extends Object> implements MonjoConverter<T>{
	

	public DefaultPojongoConverter(Class<T> objectType) {
		defaultDocumentToObjectConverter = new DefaultDocumentToObjectConverter<T>(objectType);
		defaultObjectToDocumentConverter = new DefaultObjectToDocumentConverter<T>(objectType); 
	}
	
	private DefaultDocumentToObjectConverter<T> defaultDocumentToObjectConverter;
	private DefaultObjectToDocumentConverter<T> defaultObjectToDocumentConverter;
	private String prefix;
	
	
	@Override
	public DefaultDocumentToObjectConverter<T> from(DBObject document) {
		return defaultDocumentToObjectConverter.from(document);
	}

	@Override
	public T to(){
		return defaultDocumentToObjectConverter.to();
	}
	
	@Override
	public ObjectToDocumentConverter<T> from(T javaObject) {
		defaultObjectToDocumentConverter.from(javaObject);
		return this;
	}
	@Override
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
			dbObject.put((prefix == null) ? "_id" : prefix + "._id", document.getId());
			return dbObject;
		}
		return null;
		
	}
	
	@Override
	public ObjectToDocumentConverter<T> enableUpdate() {
		defaultObjectToDocumentConverter.enableUpdate();
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> enableSearch() {
		defaultObjectToDocumentConverter.enableSearch();
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> setPrefix(String string) {
		defaultObjectToDocumentConverter.setPrefix(string);
		this.prefix = string;
		return this;
	}
	@Override
	public DBObject toDocument(BasicDBObject document) {
		return defaultObjectToDocumentConverter.toDocument(document);
	}

	@Override
	public ObjectToDocumentConverter<T> specialField(String fieldname) {
		defaultObjectToDocumentConverter.specialField(fieldname);
		return this;
	}

	
}
