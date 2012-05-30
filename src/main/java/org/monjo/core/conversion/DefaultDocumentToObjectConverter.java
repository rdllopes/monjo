package org.monjo.core.conversion;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.monjo.core.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

/**
 * Default implementation of <code>DocumentToObjectConverter</code>.
 * 
 * @author Caio Filipini
 * @see org.monjo.core.conversion.DocumentToObjectConverter
 */
public class DefaultDocumentToObjectConverter<T extends Object> implements DocumentToObjectConverter<T> {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentToObjectConverter.class);

	private DBObject document;
	private NamingStrategy namingStrategy;

	private Class<T> objectType;

	public DefaultDocumentToObjectConverter(Class<T> objectType) {
		this.objectType = objectType;
	}

	public DefaultDocumentToObjectConverter(NamingStrategy namingStrategy, Class<T> innerEntityClass) {
		this.namingStrategy = namingStrategy;
		this.objectType = innerEntityClass;
	}

	@Override
	public DefaultDocumentToObjectConverter<T> from(final DBObject document) {
		if (document == null) {
			throw new IllegalArgumentException("cannot convert a null document");
		}
		this.document = document;
		return this;
	}

	@Override
	public T to() {
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
					throw new RuntimeException("Tried to access read-only property");
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object getFieldValue(String field, PropertyDescriptor property) throws ClassNotFoundException {
		Object fieldValue = null;
		if ("id".equals(field)) {
			fieldValue = document.get("_id");
		} else {
			if (document.containsField(field)) {
				fieldValue = document.get(field);
				if (fieldValue instanceof BasicDBObject) {
					BasicDBObject basicDBObject = (BasicDBObject) fieldValue;

					Class<?> innerEntityClass = null;
					if (basicDBObject.get("_ref") != null) {
						innerEntityClass = Class.forName((String) basicDBObject.get("_ref"));
					} else {
						innerEntityClass = property.getPropertyType();
					}

					if (innerEntityClass.equals(Map.class) || Arrays.asList(innerEntityClass.getInterfaces()).contains(Map.class)) { // it's a map!
						HashMap newMap = new HashMap();
						for (Object key : ((BasicDBObject) fieldValue).keySet()) {
							Object object = ((BasicDBObject) fieldValue).get(key);
							newMap.put(key, object);
						}
						fieldValue = newMap;
					} else {
						DefaultDocumentToObjectConverter converter = new DefaultDocumentToObjectConverter(namingStrategy, innerEntityClass);
						fieldValue = converter.from(basicDBObject).to();
					}
				}
				if (fieldValue instanceof List) {
					// Covariant problem
					Method method = property.getReadMethod();
					ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
					Class<?> type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
					ArrayList newList = new ArrayList();
					List<?> list = (List<?>) fieldValue;
					for (Object object : list) {
						if (object instanceof DBObject) {
							DBObject dbObject = (DBObject) object;
							Class<?> innerEntityClass = Class.forName((String) dbObject.get("_ref"));
							DefaultDocumentToObjectConverter converter = new DefaultDocumentToObjectConverter(namingStrategy, innerEntityClass);
							newList.add(converter.from(dbObject).to());
						} else {
							newList.add(ConvertUtils.convert(object, type));
						}
					}
					fieldValue = newList;

				}
				fieldValue = applyConverters(property, fieldValue);
			}
		}
		return fieldValue;
	}

	private Object applyConverters(PropertyDescriptor property, Object fieldValue) {
		if (fieldValue != null && !property.getPropertyType().isInstance(fieldValue)) {
			fieldValue = ConvertUtils.convert(fieldValue, property.getPropertyType());
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

	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}
}
