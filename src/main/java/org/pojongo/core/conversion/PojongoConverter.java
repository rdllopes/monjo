package org.pojongo.core.conversion;

import org.hibernate.cfg.NamingStrategy;

import com.mongodb.DBObject;

public interface PojongoConverter extends ObjectToDocumentConverter,
		DocumentToObjectConverter {

	void setNamingStrategy(NamingStrategy namingStrategy);
	
	public DBObject getIdDocument(Object object);

}
