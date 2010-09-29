package org.pojongo.core.conversion;

import org.pojongo.document.IdentifiableDocument;

public class SimplePOJOWithStringId implements IdentifiableDocument<String> {
	
	private String id;
	
	public SimplePOJOWithStringId() {
	}

	@Override
	public String getId() {
		return id;
	}
	
}