package org.monjo.example;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.monjo.core.annotations.Entity;
import org.monjo.core.annotations.Id;

@Entity
public class PojoWithListInnerObject extends AbstractObject {

	private ObjectId id;
	private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public void addCategory(Category category) {
		if (categories == null) {
			categories = new ArrayList<Category>();
		}
		categories.add(category);
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
