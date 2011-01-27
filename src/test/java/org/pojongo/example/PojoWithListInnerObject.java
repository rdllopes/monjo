package org.pojongo.example;

import java.util.List;

public class PojoWithListInnerObject extends AbstractObject {
	private List<Category>  categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

}
