package org.monjo.example;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Entity;
import org.monjo.core.annotations.Transient;
import org.monjo.document.IdentifiableDocument;

@Entity
public class SimplePOJO implements IdentifiableDocument<ObjectId> {
	private Double aDoubleField;

	private String aField;

	private Long aLongField;

	private Integer anIntegerField;

	private String anotherField;

	private String aTransientField;

	private HashMap<String, String> aMapField;

	private ObjectId id;

	private Status status;

	public SimplePOJO() {
	}

	public SimplePOJO(ObjectId objectId) {
		this.id = objectId;
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

	public void generateId() {
		if (id == null) {
			id = new ObjectId();
		}

	}

	public Double getaDoubleField() {
		return aDoubleField;
	}

	public String getaField() {
		return aField;
	}

	public Long getaLongField() {
		return aLongField;
	}

	public Integer getAnIntegerField() {
		return anIntegerField;
	}

	public String getAnotherField() {
		return anotherField;
	}

	@Transient
	public String getaTransientField() {
		return aTransientField;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setaDoubleField(Double aDoubleField) {
		this.aDoubleField = aDoubleField;
	}

	public void setaField(String aField) {
		this.aField = aField;
	}

	public void setaLongField(Long aLongField) {
		this.aLongField = aLongField;
	}

	public void setAnIntegerField(Integer anIntegerField) {
		this.anIntegerField = anIntegerField;
	}

	public void setAnotherField(String anotherField) {
		this.anotherField = anotherField;
	}

	public void setaTransientField(String aTransientField) {
		this.aTransientField = aTransientField;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public HashMap<String, String> getaMapField() {
		return aMapField;
	}

	public void setaMapField(HashMap<String, String> aMapField) {
		this.aMapField = aMapField;
	}

}