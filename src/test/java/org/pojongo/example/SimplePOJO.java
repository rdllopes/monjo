package org.pojongo.example;

import org.bson.types.ObjectId;
import org.pojongo.core.conversion.Transient;
import org.pojongo.document.IdentifiableDocument;

public class SimplePOJO implements IdentifiableDocument<ObjectId> {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePOJO other = (SimplePOJO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

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