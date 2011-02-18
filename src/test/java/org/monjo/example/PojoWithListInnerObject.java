package org.monjo.example;

import java.util.ArrayList;
import java.util.List;

public class PojoWithListInnerObject extends AbstractObject {
	private List<Category>  categories;

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

}
