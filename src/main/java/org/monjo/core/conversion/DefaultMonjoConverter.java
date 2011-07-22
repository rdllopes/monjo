package org.monjo.core.conversion;

import org.bson.types.ObjectId;
import org.monjo.core.Operation;
import org.monjo.document.IdentifiableDocument;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

public class DefaultMonjoConverter<T extends Object> implements MonjoConverter<T>{
	

	public DefaultMonjoConverter(Class<T> objectType) {
		documentToObjectConverter = new DefaultDocumentToObjectConverter<T>(objectType);
		objectToDocumentConverter = new DefaultObjectToDocumentConverter<T>(objectType); 
	}
	
	private DefaultDocumentToObjectConverter<T> documentToObjectConverter;
	private DefaultObjectToDocumentConverter<T> objectToDocumentConverter;
	private String prefix;
	
	
	@Override
	public DefaultDocumentToObjectConverter<T> from(DBObject document) {
		return documentToObjectConverter.from(document);
	}

	@Override
	public T to(){
		return documentToObjectConverter.to();
	}
	
	@Override
	public ObjectToDocumentConverter<T> from(T javaObject) {
		objectToDocumentConverter.from(javaObject);
		return this;
	}
	@Override
	public DBObject toDocument() {
		return objectToDocumentConverter.toDocument();
	}
	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		documentToObjectConverter.setNamingStrategy(namingStrategy);
		objectToDocumentConverter.setNamingStrategy(namingStrategy);
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
	public ObjectToDocumentConverter<T> setPrefix(String string) {
		objectToDocumentConverter.setPrefix(string);
		this.prefix = string;
		return this;
	}
	@Override
	public DBObject toDocument(BasicDBObject document) {
		return objectToDocumentConverter.toDocument(document);
	}

	@Override
	public ObjectToDocumentConverter<T> action(Operation operation) {
		objectToDocumentConverter.action(operation);
		return this;
		
	}

}
