package org.pojongo.example;

import org.bson.types.ObjectId;
import org.pojongo.core.conversion.Transient;
import org.pojongo.document.IdentifiableDocument;

public class Example implements IdentifiableDocument<ObjectId> {
	
	private ObjectId id;
	private String name;
	private Integer number;
		
	private Long someValue;
	
	public Example() {
		someValue = 2l;
	}
	
	public Example(String name, Integer number) {
		this.name = name;
		this.number = number;
		someValue = 3l;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setSomeValue(Long someValue) {
		this.someValue = someValue;
	}

	@Override
	public ObjectId getId() {
		return id;
	}
	
	@Override
	public void setId(ObjectId id) {
		this.id = id;
		
	}
	public String getName() {
		return name;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	@Transient
	public Long getSomeValue() {
		return someValue;
	}
	
	@Override
	public String toString() {
		return "Example ["
				+ "id:" + id
				+ ", name:" + name
				+ ", number:" + number
				+ ", someValue:" + someValue
				+ "]";
	}

}
