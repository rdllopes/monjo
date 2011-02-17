package org.monjo.core.conversion;

import java.util.LinkedList;

import org.monjo.example.AnotherPojo;
import org.monjo.example.Category;
import org.monjo.example.ComplexPojo;
import org.monjo.example.PojoWithListInnerObject;
import org.monjo.example.SimplePOJO;
import org.monjo.example.User;

public class PojoBuilder {

	public static SimplePOJO createSimplePojo() {
		SimplePOJO pojo = new SimplePOJO();
		pojo.setAnIntegerField(42);
		pojo.setaLongField(43L);
		pojo.setaDoubleField(44.0);
		return pojo;
	}

	public static ComplexPojo createComplexPojo(Category category) {
		ComplexPojo complexPojo = new ComplexPojo();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		complexPojo.setCategory(category);
		complexPojo.setDescription("pojo complexo");
		return complexPojo;
	}

	public static AnotherPojo createAnotherPojo(User category) {
		AnotherPojo another = new AnotherPojo();
		another.setUser(category);
		another.setDescription("pojo complexo");
		return another;
	}

	public static PojoWithListInnerObject createMegaZordePojo() {
		Category category = new Category();
		category.setName("NewCategory");
	
		PojoWithListInnerObject pojo = new PojoWithListInnerObject();
		LinkedList<Category> categories = new LinkedList<Category>();
		categories.add(category);
		pojo.setCategories(categories);
		return pojo;
	}

}
