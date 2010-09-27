package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import com.mongodb.DBObject;

public class DocumentToObjectConverter {

	private DBObject document;
	private Mirror mirror;
	
	public DocumentToObjectConverter() {
		this.mirror = new Mirror();
	}
	
	public DocumentToObjectConverter from(DBObject document) {
		this.document = document;
		return this;
	}
	
	public <T extends Object> T to(Class<T> objectType) {
		T instance = instanceFor(objectType);
		List<Field> fields = mirror.on(objectType).reflectAll().fields();
		
		for (Field field : fields) {
			mirror.on(instance).set().field(field).withValue(document.get(field.getName()));
		}
		
		return instance;
	}

	private <T> T instanceFor(Class<T> objectType) {
		return mirror.on(objectType).invoke().constructor().withoutArgs();
	}

}
