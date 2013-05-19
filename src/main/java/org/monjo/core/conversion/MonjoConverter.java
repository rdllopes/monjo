package org.monjo.core.conversion;


import com.mongodb.DBObject;

public interface MonjoConverter<T extends Object> extends ObjectToDocumentConverter<T>,
		DocumentToObjectConverter<T> {

	public DBObject getIdDocument(Object object);

}
