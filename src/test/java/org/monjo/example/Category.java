package org.monjo.example;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Entity;
import org.monjo.core.annotations.Id;

@Entity
public class Category extends AbstractObject {
	
	private ObjectId id;
	
	private String name;

	private Long weight;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getWeight() {
		return weight;
	}

	public void setWeight(Long weight) {
		this.weight = weight;
	}

	@Id
	public ObjectId getId() {
		return id;
	}

	@Id
	public void setId(ObjectId id) {
		this.id = id;
	}

}
