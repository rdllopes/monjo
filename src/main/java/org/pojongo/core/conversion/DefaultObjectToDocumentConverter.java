package org.pojongo.core.conversion;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.cfg.NamingStrategy;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Default implementation of <code>ObjectToDocumentConverter</codse>.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.ObjectToDocumentConverter
 */
public class DefaultObjectToDocumentConverter implements
		ObjectToDocumentConverter {
	private Object javaObject;
	private NamingStrategy namingStrategy;

	/**
	 * Default constructor.
	 */
	DefaultObjectToDocumentConverter() {
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
			throw new IllegalStateException(
					"cannot convert a null object, please call from(Object) first!");
		}

		PropertyDescriptor[] descriptors = PropertyUtils
				.getPropertyDescriptors(javaObject.getClass());
		DBObject document = new BasicDBObject();

		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod.isAnnotationPresent(Transient.class)) {
				continue;
			}
			String fieldName = descriptor.getName();
			if ("class".equals(fieldName)) continue;
			Object fieldValue;
			try {
				fieldValue = readMethod.invoke(javaObject);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (fieldValue == null)
				continue;
			if (fieldValue.getClass().isEnum()) {
				fieldValue = fieldValue.toString();
			}
			String documentFieldName = fieldName;
			if (fieldName.indexOf("$") >= 0) {
				continue;
			}
			if ("id".equals(fieldName)) {
				documentFieldName = "_id";
			} else {
				documentFieldName = namingStrategy
						.propertyToColumnName(fieldName);
			}
			document.put(documentFieldName, fieldValue);
		}

		return document;
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

}
