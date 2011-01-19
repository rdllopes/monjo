package org.pojongo.example;

import java.util.Date;

public class SubClassPojo extends SimplePOJO{
	private String extraProperty;
	private Date initialDate;
	
	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public String getExtraProperty() {
		return extraProperty;
	}

	public void setExtraProperty(String extraProperty) {
		this.extraProperty = extraProperty;
	}

}
