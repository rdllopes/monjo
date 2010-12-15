package org.pojongo.example;

import org.bson.types.ObjectId;
import org.pojongo.document.IdentifiableDocument;

public abstract class AbstractObject implements IdentifiableDocument<ObjectId>{

	private ObjectId objectId;

	@Override
	public ObjectId getId() {
		return objectId;
	}

	@Override
	public void setId(ObjectId id) {
 		this.objectId = id;
	}
}
