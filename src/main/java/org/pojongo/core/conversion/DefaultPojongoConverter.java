package org.pojongo.core.conversion;

import org.hibernate.cfg.NamingStrategy;

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

	
}
