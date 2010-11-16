package org.pojongo.core.conversion;

import org.bson.types.ObjectId;
import org.pojongo.document.IdentifiableDocument;

public class SimplePOJO implements IdentifiableDocument<ObjectId> {
	public enum Status{
		NEW,
		EDITED, LOCK;
	}
	
	private Status status;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	private ObjectId id;
	private String aField;
	private String anotherField;
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getaField() {
		return aField;
	}

	public void setaField(String aField) {
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

	public Double getaDoubleField() {
		return aDoubleField;
	}

	public void setaDoubleField(Double aDoubleField) {
		this.aDoubleField = aDoubleField;
	}

	public Long getaLongField() {
		return aLongField;
	}

	public void setaLongField(Long aLongField) {
		this.aLongField = aLongField;
	}

	@Transient
	public String getaTransientField() {
		return aTransientField;
	}

	public void setaTransientField(String aTransientField) {
		this.aTransientField = aTransientField;
	}

	private Integer anIntegerField;
	private Double aDoubleField;
	private Long aLongField;
		
	private String aTransientField;
	
	public SimplePOJO() {
	}
	
	public SimplePOJO(ObjectId objectId) {
		this.id = objectId;
	}

	public void generateId() {
		if (id == null){
			id = new ObjectId();
		}
		
	}

}