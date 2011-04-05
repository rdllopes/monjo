package org.monjo.example;

import org.monjo.core.annotations.Entity;
import org.monjo.document.IdentifiableDocument;

@Entity
public class SimplePOJOWithStringId implements IdentifiableDocument<String> {
	
	private String id;
	
	public SimplePOJOWithStringId() {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
}