package org.monjo.example;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Id;

public class AnotherPojo extends AbstractObject {

	private ObjectId id;
	private User user;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String description;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
