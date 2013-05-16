package org.monjo.example;

import org.monjo.core.annotations.Entity;
import org.monjo.core.annotations.Id;

@Entity
public class SimplePOJOWithStringId  {

	private String id;

	public SimplePOJOWithStringId() {
	}

	@Id
	public String getId() {
		return id;
	}

	@Id
	public void setId(String id) {
		this.id = id;
	}

}