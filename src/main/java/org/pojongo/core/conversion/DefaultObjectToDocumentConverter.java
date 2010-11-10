package org.pojongo.core.conversion;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.cfg.NamingStrategy;

import net.vidageek.mirror.dsl.Mirror;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Default implementation of <code>ObjectToDocumentConverter</code>.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.ObjectToDocumentConverter
 */
public class DefaultObjectToDocumentConverter implements ObjectToDocumentConverter {

	private final Mirror mirror;
	private Object javaObject;
	private NamingStrategy namingStrategy;
	
	/**
	 * Default constructor.
	 */
	DefaultObjectToDocumentConverter() {
		this.mirror = new Mirror();
	}

	@Override
	public ObjectToDocumentConverter from(final Object javaObject) {
		if (javaObject == null) {
			throw new IllegalArgumentException("cannot convert a null object");
		}
		this.javaObject = javaObject;
		return this;
	}
	
	@Override
	public DBObject toDocument() {
		if (javaObject == null) {
			throw new IllegalStateException("cannot convert a null object, please call from(Object) first!");
		}
		
		List<Field> fields = getFieldsFor(javaObject.getClass());
		DBObject document = new BasicDBObject();
		
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Transient.class)) {
				String fieldName = field.getName();
				Object fieldValue =  mirror.on(javaObject).get().field(field);
				
				if (fieldValue != null) {
					String documentFieldName = fieldName;
					if ("id".equals(fieldName)) {
						documentFieldName = "_id";
					} else {
						documentFieldName = namingStrategy.propertyToColumnName(fieldName);
					}
					document.put(documentFieldName, fieldValue);					
				}
			}
		}
		
		return document;
	}
	
	private <T> List<Field> getFieldsFor(final Class<T> objectType) {
		return mirror.on(objectType).reflectAll().fields();
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

}
