package org.monj.example;

import org.monjo.document.IdentifiableDocument;

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