package org.monjo.core.conversion;

import org.hibernate.cfg.NamingStrategy;

import com.mongodb.DBObject;

public interface MonjoConverter extends ObjectToDocumentConverter,
		DocumentToObjectConverter {

	void setNamingStrategy(NamingStrategy namingStrategy);
	
	public DBObject getIdDocument(Object object);

}
