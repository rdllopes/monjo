package org.monjo.example;

import org.monjo.core.annotations.Entity;
import org.monjo.document.IdentifiableDocument;

@Entity
public class IntegerId implements IdentifiableDocument<Integer>{
	
	private Integer id;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;		
	}

}
