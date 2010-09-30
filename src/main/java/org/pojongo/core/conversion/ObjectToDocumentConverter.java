package org.pojongo.core.conversion;

import com.mongodb.DBObject;

public interface ObjectToDocumentConverter {

	ObjectToDocumentConverter from(Object object);

	DBObject toDocument();

}
