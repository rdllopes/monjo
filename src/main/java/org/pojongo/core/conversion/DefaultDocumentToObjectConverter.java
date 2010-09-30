package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import com.mongodb.DBObject;

/**
 * Default implementation of <code>DocumentToObjectConverter</code>.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.DocumentToObjectConverter
 */
public class DefaultDocumentToObjectConverter implements DocumentToObjectConverter {

	private final Mirror mirror;
	private DBObject document;
	
	/**
	 * Default constructor.
	 */
	public DefaultDocumentToObjectConverter() {
		this.mirror = new Mirror();
	}
	
	@Override
	public DefaultDocumentToObjectConverter from(final DBObject document) {
		if (document == null) {
			throw new IllegalArgumentException("cannot convert a null document");
		}
		this.document = document;
		return this;
	}
	
	@Override
	public <T extends Object> T to(final Class<T> objectType)
			throws IllegalStateException, IllegalArgumentException {
		if (document == null) {
			throw new IllegalStateException("cannot convert a null document, please call from(DBObject) first!");
		}
		
		T instance = instanceFor(objectType);
		List<Field> fields = getFieldsFor(objectType);
		
		for (Field field : fields) {
			String fieldName = field.getName();
			if ("id".equals(fieldName)) {
				mirror.on(instance).set().field(field).withValue(document.get("_id"));
			} else if (document.containsField(fieldName)) {
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
