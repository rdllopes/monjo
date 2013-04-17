package org.monjo.example;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Entity;
import org.monjo.core.annotations.Id;
import org.monjo.document.IdentifiableDocument;

@Entity
public class ListWithin implements IdentifiableDocument<ObjectId> {

	public ListWithin() {
	}

	private ObjectId id;

	private List<String> names;

	private List<Integer> groups;

	@Id
	public ObjectId getId() {
		return id;
	}

	@Id
	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Integer> getGroups() {
		return groups;
	}

	public void setGroups(List<Integer> groups) {
		this.groups = groups;
	}

	public void addName(int i) {
		if (names == null) {
			names = new ArrayList<String>();
		}
		names.add(Integer.toString(i));
	}

	public void addGroup(int i) {
		if (groups == null) {
			groups = new ArrayList<Integer>();
		}
		groups.add(i);
	}

}