package org.monjo.example;

import org.monjo.core.annotations.Reference;



public class ComplexPojo extends AbstractObject {
	
	private Category category;
	
	public Integer getTest() {
		return test;
	}

	public void setTest(Integer test) {
		this.test = test;
	}

	private Integer test;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String description;

	@Reference
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	

}
