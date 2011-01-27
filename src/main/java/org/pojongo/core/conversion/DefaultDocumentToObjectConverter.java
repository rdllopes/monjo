package org.pojongo.core.conversion;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.cfg.NamingStrategy;
import org.pojongo.document.IdentifiableDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Default implementation of <code>DocumentToObjectConverter</code>.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.DocumentToObjectConverter
 */
public class DefaultDocumentToObjectConverter implements DocumentToObjectConverter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentToObjectConverter.class);

	// private final Mirror mirror;
	private DBObject document;
	private NamingStrategy namingStrategy;

	/**
	 * Default constructor.
	 */
	DefaultDocumentToObjectConverter() {
		// this.mirror = new Mirror();
	}

	DefaultDocumentToObjectConverter(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
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
	public <T extends Object> T to(final Class<T> objectType) {
		if (document == null) {
			throw new IllegalStateException("cannot convert a null document, please call from(DBObject) first!");
		}

		T instance = instanceFor(objectType);
		PropertyDescriptor[] desc = getFieldsFor(objectType);

		String field = null;
		for (PropertyDescriptor property : desc) {
			try {
				field = property.getName();
				if ("class".equals(field)) {
					continue;
				}
				if (property.getReadMethod().isAnnotationPresent(Transient.class)) {
					continue;
				}
				field = namingStrategy.propertyToColumnName(field);
				Object fieldValue = getFieldValue(field, property);
				Method writeMethod = property.getWriteMethod();
				if (writeMethod == null) {
					throw new RuntimeException("Tentativa de acessar propriedade somente para leitura");
				}
				writeMethod.invoke(instance, fieldValue);
			} catch (RuntimeException e) {
				logger.error("fail in {} using {}.", field, document);
				throw e;
			} catch (Exception e) {
				logger.error("fail in {} using {}.", field, document);
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private Object getFieldValue(String field, PropertyDescriptor property) throws ClassNotFoundException {
		Object fieldValue = null;				
		if ("id".equals(field)) {
			fieldValue = document.get("_id");
		} else {
			if (document.containsField(field)) {
				fieldValue = document.get(field);
				if (fieldValue instanceof BasicDBObject) {
					BasicDBObject basicDBObject = (BasicDBObject) fieldValue;
					String typeName = (String) basicDBObject.get("_type");
					if ("reference".equals(typeName)) {
						Class<?> innerEntityClass = Class.forName((String) basicDBObject.get("_ref"));
						DefaultDocumentToObjectConverter converter = new DefaultDocumentToObjectConverter(namingStrategy);
						fieldValue = converter.from(basicDBObject).to(innerEntityClass);
					}
				}
				if (fieldValue instanceof List) {
					// Covariant problem 
					List newList = new ArrayList();
					List<?> list = (List<?>) fieldValue;
					for (Object object : list) {
						if (object instanceof DBObject) {
							DBObject dbObject = (DBObject) object;
							Class<?> innerEntityClass = Class.forName((String) dbObject.get("_ref"));
							DefaultDocumentToObjectConverter converter = new DefaultDocumentToObjectConverter(namingStrategy);
							newList.add(converter.from(dbObject).to(innerEntityClass));
						} else {
							newList.add(object);
						}
					}
					fieldValue = newList;
					
				}
				if (fieldValue != null && !property.getPropertyType().isInstance(fieldValue)) {
					fieldValue = ConvertUtils.convert(fieldValue, property.getPropertyType());
				}						
			}
		}
		return fieldValue;
	}

	private <T> PropertyDescriptor[] getFieldsFor(final Class<T> objectType) {
		try {
			return PropertyUtils.getPropertyDescriptors(objectType);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T instanceFor(final Class<T> objectType) {
		if (objectType == null)
			throw new IllegalArgumentException();
		try {
			return objectType.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}
}
