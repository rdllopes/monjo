package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

import com.mongodb.DBObject;

/**
 * Class responsible for converting <code>DBObject</code> instances<br>
 * to a given Java Object based on the matching fields.
 * 
 * @author Caio Filipini
 */
public class DefaultDocumentToObjectConverter {

	private final Mirror mirror;
	private DBObject document;
	
	/**
	 * Default constructor.
	 */
	public DefaultDocumentToObjectConverter() {
		this.mirror = new Mirror();
	}
	
	/**
	 * Builder method used to configure which <code>DBObject</code> should be converted.
	 * 
	 * @param document the <code>DBObject</code> to be converted to Java object.
	 * @return the converter.
	 * @throws IllegalArgumentException if <code>document</code> is null.
	 */
	public DefaultDocumentToObjectConverter from(final DBObject document) {
		if (document == null) {
			throw new IllegalArgumentException("cannot convert a null document");
		}
		this.document = document;
		return this;
	}
	
	/**
	 * Converts the previously configured <code>DBObject</code> to a corresponding instance<br />
	 * of the specified Java class <code>objectType</code>.<br /><br/>
	 * 
	 * The conversion is done by reflecting <code>objectType</code>'s attributes and finding<br />
	 * corresponding fields in <code>DBObject<code>. If a matching field is found, its value is<br />
	 * set on the target object, preserving the data type returned by MongoDB's driver.
	 * 
	 * @param <T> the generic type for objectType.
	 * @param objectType the type to be converted to.
	 * @return an instance of <code>objectType</code> populated with corresponding values from MongoDB's document.
	 */
	public <T extends Object> T to(final Class<T> objectType) {
		T instance = instanceFor(objectType);
		List<Field> fields = getFieldsFor(objectType);
		
		for (Field field : fields) {
			String fieldName = field.getName();
			if ("id".equals(fieldName)) {
				mirror.on(instance).set().field(field).withValue(document.get("_id"));
				continue;
			}
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
