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
	
	public void setAField(String aField) {
		this.aField = aField;
	}

	public String getAnotherField() {
		return anotherField;
	}
	
	public void setAnotherField(String anotherField) {
		this.anotherField = anotherField;
	}
	
	public Integer getAnIntegerField() {
		return anIntegerField;
	}
	
	public void setAnIntegerField(Integer anIntegerField) {
		this.anIntegerField = anIntegerField;
	}
	
	public Double getADoubleField() {
		return aDoubleField;
	}	
	
	public void setADoubleField(Double aDoubleField) {
		this.aDoubleField = aDoubleField;
	}
	
	public Long getALongField() {
		return aLongField;
	}
	
	public void setALongField(Long aLongField) {
		this.aLongField = aLongField;
	}

	@Override
	public ObjectId getId() {
		return id;
	}
	
	public void generateId() {
		if (this.id == null) {
			this.id = new ObjectId();
		}
	}

}