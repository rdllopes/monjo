package org.pojongo.example;

import org.pojongo.document.IdentifiableDocument;

public class SimplePOJOWithStringId implements IdentifiableDocument<String> {
	
	private String id;
	
	public SimplePOJOWithStringId() {
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}