package org.pojongo.example;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.pojongo.document.IdentifiableDocument;

public class ListWithin implements IdentifiableDocument<ObjectId>{
	
	public ListWithin() {
	}
	
	private ObjectId id;
	
	private List<String> names;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void addItem(int i) {
		if (names == null){
			names = new ArrayList<String>();
		}
		names.add(Integer.toString(i));
	}
	
}