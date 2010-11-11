package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.cfg.NamingStrategy;

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
	private NamingStrategy namingStrategy;
	
	/**
	 * Default constructor.
	 */
	DefaultDocumentToObjectConverter() {
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T to(final Class<T> objectType)
			throws IllegalStateException, IllegalArgumentException {
		if (document == null) {
			throw new IllegalStateException("cannot convert a null document, please call from(DBObject) first!");
		}
		
		T instance = instanceFor(objectType);
		List<Field> fields = getFieldsFor(objectType);
		
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Transient.class)) {
				String fieldName = field.getName();
				Class classField = field.getType();
				
				// TODO in fact, every final field should be skipped
				if (fieldName.equals("serialVersionUID")) continue;
				Object fieldValue = null;
				if ("id".equals(fieldName)) {
					fieldValue = document.get("_id");
				} else {
					fieldName = namingStrategy.propertyToColumnName(fieldName);
					if (document.containsField(fieldName)) {
						fieldValue = document.get(namingStrategy.propertyToColumnName(fieldName));
						if (classField.isEnum()){
							fieldValue = Enum.valueOf(classField, (String) fieldValue);
							
						}						
					}
				}
				
				mirror.on(instance).set().field(field).withValue(fieldValue);
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

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}
}
