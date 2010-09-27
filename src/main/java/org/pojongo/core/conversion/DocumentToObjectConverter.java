package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import com.mongodb.DBObject;

public class DocumentToObjectConverter {

	private final Mirror mirror;
	private DBObject document;
	
	public DocumentToObjectConverter() {
		this.mirror = new Mirror();
	}
	
	public DocumentToObjectConverter from(final DBObject document) {
		if (document == null) {
			throw new IllegalArgumentException("cannot convert a null document");
		}
		this.document = document;
		return this;
	}
	
	public <T extends Object> T to(final Class<T> objectType) {
		T instance = instanceFor(objectType);
		List<Field> fields = getFieldsFor(objectType);
		
		for (Field field : fields) {
			String fieldName = field.getName();
			if (document.containsField(fieldName)) {
				mirror.on(instance).set().field(field).withValue(document.get(fieldName));
			}
		}
		
		return instance;
	}

	private <T> List<Field> getFieldsFor(final Class<T> objectType) {
		return mirror.on(objectType).reflectAll().fields();
	}

	private <T> T instanceFor(final Class<T> objectType) {
		return mirror.on(objectType).invoke().constructor().withoutArgs();
	}

}
