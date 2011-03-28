package org.monjo.core.conversion;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import org.hibernate.cfg.NamingStrategy;
import org.monjo.core.annotations.Reference;
import org.monjo.core.annotations.Transient;
import org.monjo.document.IdentifiableDocument;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Default implementation of <code>ObjectToDocumentConverter</codse>.
 * 
 * @author Caio Filipini
 * @author Rodrigo di Lorenzo Lopes
 * @see org.monjo.core.conversion.ObjectToDocumentConverter
 */
public class DefaultObjectToDocumentConverter<T> implements ObjectToDocumentConverter<T> {
	private Object javaObject;
	private NamingStrategy namingStrategy;
	private boolean update = false;
	private boolean innerObject;
	private boolean search;
	private String prefix;
	private Class<T> objectType;
	private String specialField;
	private boolean innerUpdate;

	
	public DefaultObjectToDocumentConverter(NamingStrategy namingStrategy, Class<T> objectType) {
		if (objectType == null) throw new NullPointerException();
		this.objectType = objectType;
		this.namingStrategy = namingStrategy;
		
	}

	public DefaultObjectToDocumentConverter(Class<T> objectType) {
		if (objectType == null) throw new NullPointerException();
		this.objectType = objectType;
	}

	@Override
	public ObjectToDocumentConverter<T> from(final T javaObject) {
		if (javaObject == null) {
			throw new IllegalArgumentException("cannot convert a null object");
		}
		this.javaObject = javaObject;
		return this;
	}

	@Override
	public DBObject toDocument() {
		return toDocument(new BasicDBObject());
	}

	@Override
	public DBObject toDocument(BasicDBObject document) {
		if (javaObject == null) {
			throw new IllegalStateException("cannot convert a null object, please call from(Object) first!");
		}
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(objectType);

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
			if (fieldValue == null) {
				if ("id".equals(fieldName) && !(search || update || innerObject)) {
					ObjectId objectId = new ObjectId();
					if (javaObject instanceof IdentifiableDocument) {
						IdentifiableDocument<ObjectId> identifiableDocument = (IdentifiableDocument<ObjectId>) javaObject;
						// TODO retirar essa bomba relogio daqui (veja
						// literatura sobre type erasure)
						identifiableDocument.setId(objectId);
					}
					fieldValue = objectId;
				} else {
					continue;
				}
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
			if (prefix != null) {
				documentFieldName = prefix + documentFieldName; 				
			}
						
			fieldValue = getFieldValue(document, readMethod, fieldValue, documentFieldName);
			if (fieldValue != null) {
				if (innerUpdate && fieldName.equals(specialField)) {
					BasicDBObject basicDBObject = (BasicDBObject) fieldValue;
					Set<String> keys = basicDBObject.keySet();
					for (String key : keys) {
						document.put(key, basicDBObject.get(key));
					}
				} else {
					document.put(documentFieldName, fieldValue);
				}
				
			}
				
		}
		if (update) {
			document = createSetUpdate(document);
		}
		return document;
	}

	private Object getFieldValue(BasicDBObject document, Method readMethod, Object fieldValue, String fieldName) {
		Class<? extends Object> clasz = fieldValue.getClass();
		if (isEnumWorkAround(clasz)) {
			fieldValue = fieldValue.toString();
		} else if (fieldValue instanceof List) {
			List list = (List) fieldValue;
			if (list.size() == 0)
				return null;	
			if (search || innerUpdate) {
				if (list.size() > 1)
					fieldValue = new BasicDBObject("$or", getDbList(document, readMethod, fieldName, list));
				else 
					fieldValue = getFieldValue(document, readMethod, list.get(0), fieldName);
			} else {
				fieldValue = getDbList(document, readMethod, fieldName, list);
			}
		} else if (fieldValue instanceof IdentifiableDocument) {
			fieldValue = getFieldValueIdentifiable(document, readMethod, fieldValue, fieldName);
		} else if (!(fieldValue instanceof Serializable)) {
			DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, fieldValue.getClass());
			DBObject innerBasicDBObject = converter.from(fieldValue).toDocument();
			innerBasicDBObject.put("_ref", clasz.getCanonicalName());
			fieldValue = innerBasicDBObject;
		}
		return fieldValue;
	}

	private boolean isEnumWorkAround(Class<? extends Object> enumClass) {
		 while ( enumClass.isAnonymousClass() ) {
		      enumClass = enumClass.getSuperclass();
		    }
	    return enumClass.isEnum();
	}

	private BasicDBList getDbList(BasicDBObject document, Method readMethod, String fieldName, List list) {
		BasicDBList dbList = new BasicDBList();
		for (Object object : list) {
			dbList.add(getFieldValue(document, readMethod, object, fieldName));
		}
		return dbList;
	}

	private Object getFieldValueIdentifiable(BasicDBObject document, Method readMethod, Object element, String fieldName) {
		DBObject innerBasicDBObject;
		if (readMethod.isAnnotationPresent(Reference.class)) {
			IdentifiableDocument<?> identifiable = (IdentifiableDocument<?>) element;
			innerBasicDBObject = new BasicDBObject();
			innerBasicDBObject.put("_id", identifiable.getId());
			innerBasicDBObject.put("_ref", element.getClass().getCanonicalName());
		} else {
			DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, element.getClass());
			if (search) {
				converter.from(element).setPrefix(prefix != null ? prefix + fieldName + "."  : fieldName + "." ).toDocument(document);
				return null;
			} 
			else if (innerUpdate && fieldName.equals(specialField)) {
				innerBasicDBObject = converter.enableInnerObject().from(element).setPrefix(fieldName + ".$.").toDocument();
			}
			else { 
				innerBasicDBObject = converter.from(element).toDocument();
				innerBasicDBObject.put("_ref", element.getClass().getCanonicalName());
			}
		}
		return innerBasicDBObject;
	}

	private ObjectToDocumentConverter enableInnerObject() {
		innerObject = true;
		return this;
	}

	private BasicDBObject createSetUpdate(DBObject document) {
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("$set", document);
		return dbObject;
	}

	public void setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
	}

	@Override
	public ObjectToDocumentConverter<T> enableUpdate() {
		this.update = true;
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> enableSearch() {
		this.search = true;
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> setPrefix(String string) {
		this.prefix = string;
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> specialField(String fieldname) {
		this.specialField = fieldname;
		this.innerUpdate = true;
		return this;
	}

}
