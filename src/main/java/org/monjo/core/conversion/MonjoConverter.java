package org.monjo.core.conversion;

import org.hibernate.cfg.NamingStrategy;

import com.mongodb.DBObject;

public interface MonjoConverter<T extends Object> extends ObjectToDocumentConverter<T>,
		DocumentToObjectConverter<T> {

	@Override
	void setNamingStrategy(NamingStrategy namingStrategy);
	
	public DBObject getIdDocument(Object object);

}
