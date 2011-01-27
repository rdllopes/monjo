package org.pojongo.example;

import java.util.List;

public class PojoWithListInnerObject extends AbstractObject {
	private List<Category>  category;

	public List<Category> getCategory() {
		return category;
	}

	public void setCategory(List<Category> category) {
		this.category = category;
	}

}
