package org.pojongo.example;

import org.apache.commons.beanutils.Converter;
import org.pojongo.example.SimplePOJO.Status;

public class StatusConverter implements Converter{

	@Override
	public Object convert(Class type, Object value) {
		if (value == null) return null;
		if (value instanceof String) {
			String string = (String) value;
			return convert(string, type);
		}
		if (value.getClass().equals(type)){
			return value;
		}
		throw new RuntimeException("Conversão não conhecida");
	}

	private Object convert(String string, Class type) {
		if (type.equals(Status.class)){
			return Status.valueOf(string);
		}
		throw new RuntimeException("Conversão não conhecida");
	}

}
