package org.pojongo.core.conversion;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.cfg.NamingStrategy;
import org.pojongo.document.IdentifiableDocument;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Default implementation of <code>ObjectToDocumentConverter</codse>.
 * 
 * @author Caio Filipini
 * @see org.pojongo.core.conversion.ObjectToDocumentConverter
 */
public class DefaultObjectToDocumentConverter implements ObjectToDocumentConverter {
	private Object javaObject;
	private NamingStrategy namingStrategy;
	private boolean update;

	/**
	 * Default constructor.
	 */
	DefaultObjectToDocumentConverter() {
		update = false;
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
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(javaObject.getClass());
		DBObject document = new BasicDBObject();

		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod.isAnnotationPresent(Transient.class)) {
				continue;
			}
			String fieldName = descriptor.getName();
			if ("class".equals(fieldName))
				continue;
			Object fieldValue;
			try {
				fieldValue = readMethod.invoke(javaObject);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (fieldValue == null)
				continue;
			Class<? extends Object> clasz = fieldValue.getClass();
			if (clasz.isEnum()) {
				fieldValue = fieldValue.toString();
			} else if (fieldValue instanceof List) {
				List list = (List) fieldValue;
				if (list.size() == 0)
					continue;
				Object firstElement = list.get(0);
				if (firstElement instanceof IdentifiableDocument) {
					BasicDBObject basicDBObject = new BasicDBObject("$type", "list");
					BasicDBList dbList = new BasicDBList();
					for (Object object : list) {
						IdentifiableDocument<?> document2 = (IdentifiableDocument<?>) object;
						basicDBObject.put("_id", document2.getId());
						basicDBObject.put("$ref", document2.getClass().getCanonicalName());
						dbList.add(basicDBObject);
					}
					basicDBObject.put("$list", dbList);
					fieldValue = dbList;
				}
			} else if (fieldValue instanceof IdentifiableDocument) {
				IdentifiableDocument<?> identifiableDocument = (IdentifiableDocument<?>) fieldValue;
				BasicDBObject object = new BasicDBObject("$type", "reference");
				object.put("$ref", clasz.getCanonicalName());
				object.put("_id", identifiableDocument.getId());
				fieldValue = object;
			}
			String documentFieldName = fieldName;
			if (fieldName.indexOf("$") >= 0) {
				continue;
			}
			if ("id".equals(fieldName)) {
				if (update) {
					continue;
				}
				documentFieldName = "_id";
			} else {
				documentFieldName = namingStrategy.propertyToColumnName(fieldName);
			}

			document.put(documentFieldName, fieldValue);
		}
		if (update) {
			document = createSetUpdate(document);
		}

		return document;
	}

	private DBObject createSetUpdate(DBObject document) {
		DBObject dbObject = new BasicDBObject();
		dbObject.put("$set", document);
		return dbObject;
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	@Override
	public ObjectToDocumentConverter enableUpdate() {
		this.update = true;
		return this;
	}

}
