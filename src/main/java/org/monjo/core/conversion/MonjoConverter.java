package org.monjo.core.conversion;


import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

public interface MonjoConverter<T extends Object> extends ObjectToDocumentConverter<T>,
		DocumentToObjectConverter<T> {

	@Override
	void setNamingStrategy(NamingStrategy namingStrategy);
	
	public DBObject getIdDocument(Object object);

}
