package org.pojongo.core.conversion;

import org.bson.types.ObjectId;
import org.pojongo.document.IdentifiableDocument;

public class SimplePOJO implements IdentifiableDocument<ObjectId> {
	
	private ObjectId id;
	private String aField;
	private String anotherField;
	private Integer anIntegerField;
	private Double aDoubleField;
	private Long aLongField; // that's poetic :)
	
	public SimplePOJO() {
	}

	public String getAField() {
		return aField;
	}

	public String getAnotherField() {
		return anotherField;
	}
	
	public Integer getAnIntegerField() {
		return anIntegerField;
	}
	
	public Double getADoubleField() {
		return aDoubleField;
	}	
	
	public Long getALongField() {
		return aLongField;
	}

	@Override
	public ObjectId getId() {
		return id;
	}
	
}