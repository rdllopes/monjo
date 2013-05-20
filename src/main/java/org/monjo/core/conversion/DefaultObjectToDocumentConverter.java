package org.monjo.core.conversion;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.bson.types.ObjectId;
import org.monjo.core.Operation;
import org.monjo.core.annotations.Indexed;
import org.monjo.core.annotations.Reference;
import org.monjo.core.annotations.Transient;
import org.monjo.document.DirtFieldsWatcher;
import static org.monjo.core.conversion.ConverterUtils.isEntity;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

/**
 * Default implementation of <code>ObjectToDocumentConverter</code>.
 * 
 * @author Caio Filipini
 * @author Rodrigo di Lorenzo Lopes
 * @see org.monjo.core.conversion.ObjectToDocumentConverter
 */
public class DefaultObjectToDocumentConverter<T> implements ObjectToDocumentConverter<T> {
	private Object javaObject;
	private final NamingStrategy namingStrategy;
	private final Class<T> objectType;
	
	private Operation operation = Operation.Insert;
	private BasicDBObject setUpdate;
	private String prefix;
	private HashSet<String> dirtFields;
	private boolean dirtWatcher;
	private boolean skip;
	private BasicDBObject rootDocument;

	public DefaultObjectToDocumentConverter(NamingStrategy namingStrategy, Class<T> objectType) {
		if (objectType == null)
			throw new NullPointerException();
		this.objectType = objectType;
		this.namingStrategy = namingStrategy;
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
		this.rootDocument = new BasicDBObject();
		return process();
	}

	@Override
	public DBObject toDocument(BasicDBObject rootDocument2) {
		this.rootDocument = rootDocument2;
		return process();
	}

	private DBObject process() {
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(objectType);
		if (javaObject == null) {
			throw new IllegalStateException("cannot convert a null object, please call from(Object) first!");
		}
		if (prefix == null && isUpdateAction()) {
			setUpdate = new BasicDBObject();
		}
		dirtFields = new HashSet<String>();
		if (javaObject instanceof DirtFieldsWatcher) {
			dirtWatcher = true;
			DirtFieldsWatcher internalMonjoObject = (DirtFieldsWatcher) javaObject;
			Set<String> temp = internalMonjoObject.dirtFields();
			for (String name : temp) {
				char propName[] = name.substring("set".length()).toCharArray();
				propName[0] = Character.toLowerCase(propName[0]);
				dirtFields.add(new String(propName));
			}
		}

		for (PropertyDescriptor descriptor : descriptors) {
			Method readMethod = descriptor.getReadMethod();
			if (readMethod.isAnnotationPresent(Transient.class)) {
				continue;
			}
			
			String fieldName = descriptor.getName();
			if ("class".equals(fieldName))
				continue;
			Field field = null;
			boolean virtualMethod = false;
			try {
				field = objectType.getField(fieldName);
				if (Modifier.isTransient(field.getModifiers())){
					continue ;
				}				
			} catch (NoSuchFieldException e1) {
				virtualMethod = true;
			}

			Object fieldValue;
			try {
				fieldValue = readMethod.invoke(javaObject);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			// tratamento para valores nulos
			// preencher o id caso seja insert ... não faze-lo na busca,
			// atualização ou em innerObjects
			if (fieldValue == null && "id".equals(fieldName) && operation.equals(Operation.Insert)) {
				ObjectId objectId = new ObjectId();
				// trocar por annotation
				if (isEntity(javaObject)) {
					// TODO retirar essa bomba relogio daqui (veja
					// literatura sobre type erasure)
					AnnotatedDocumentId.set(javaObject, objectId);
				}
				fieldValue = objectId;
			}
			if (fieldName.indexOf("$") >= 0) {
				continue;
			}

			String documentFieldName = fieldName;
			if ("id".equals(fieldName)) {
				if (operation.equals(Operation.Update) || operation.equals(Operation.UpdateWithAddSet)) {
					continue;
				}
				documentFieldName = getDocumentIdFieldName();
			} else {
				documentFieldName = getDocumentFieldName(fieldName);
			}
			if (isASkipField(fieldName)) {
				continue;
			}
			skip = false;
			fieldValue = processField(readMethod, fieldValue, documentFieldName);
			if (skip)
				continue;

			putAnotherKeyValueInDocument(fieldName, fieldValue, documentFieldName);

		}
		if (prefix == null) {
			switch (operation) {
			case Update:
			case UpdateInnerObject:
			case UpdateWithAddSet:
				rootDocument.put("$set", setUpdate);			
			default:
				// nada a fazer
			}
		}
		return rootDocument;
	}

	private boolean isUpdateAction() {
		switch (operation) {
		case Update:
		case UpdateInnerObject:
		case UpdateWithAddSet:
			return true;
		}
		return false;
	}

	private String getDocumentIdFieldName() {
		String documentFieldName;
		documentFieldName = "_id";
		if (prefix != null) {
			documentFieldName = prefix + documentFieldName;
		}
		return documentFieldName;
	}

	private String getDocumentFieldName(String fieldName) {
		String documentFieldName;
		documentFieldName = namingStrategy.propertyToColumnName(fieldName);
		if (prefix != null) {
			documentFieldName = prefix + documentFieldName;
		}
		return documentFieldName;
	}

	private void putAnotherKeyValueInDocument(String fieldName, Object fieldValue, String documentFieldName) {
		if (fieldValue != null || dirtWatcher) {
			if (isUpdateAction()) {
				setUpdate.put(documentFieldName, fieldValue);
			} else {
				rootDocument.put(documentFieldName, fieldValue);
			}

		}
	}

	private boolean isASkipField(String fieldName) {
		return dirtWatcher && !dirtFields.contains(fieldName);
	}

	/**
	 * 
	 * @param document
	 * @param readMethod
	 * @param fieldValue
	 * @param fieldName
	 * @return fieldValue. OBS: side effect... could change skip and document
	 *         properties
	 */
	private Object processField(Method readMethod, Object fieldValue, String fieldName) {
		if (fieldValue == null)
			return null;
		Class<? extends Object> clasz = fieldValue.getClass();
		if (isEnumWorkAround(clasz)) {
			fieldValue = fieldValue.toString();
		} else if (fieldValue instanceof Collection) {
			Collection<?> collection = (Collection<?>) fieldValue;
			fieldValue = processFieldCollection(readMethod, fieldName, collection);
		} else if (isEntity(fieldValue)) {
			fieldValue = processEntity(readMethod, fieldValue, fieldName);
		} else if (!(fieldValue instanceof Serializable)) {
			DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, fieldValue.getClass());
			DBObject innerBasicDBObject = converter.from(fieldValue).toDocument();
			innerBasicDBObject.put("_ref", clasz.getCanonicalName());
			fieldValue = innerBasicDBObject;
		}
		return fieldValue;
	}

	private Object processFieldCollection(Method readMethod, String fieldName, Collection<?> collection) {
		switch (operation) {
		case Search:
			if (collection == null) {
				return null;
			}
			if (collection.size() == 1) {
				return processField(readMethod, collection.toArray()[0], fieldName);
			} else {
				return getDbList(readMethod, fieldName, collection);
			}
		case Insert:
			return getDbList(readMethod, fieldName, collection);
		case Update:
			if (collection.isEmpty()) {
				return getDbList(readMethod, fieldName, collection);
			}
			return getDbList(readMethod, fieldName, collection);
		case UpdateInnerObject:
			if (collection.size() != 1) {
				throw new IllegalArgumentException("It is not possible update multiples collection, using positional operator.");
			}
			Object uniqueElement = collection.toArray()[0];
			if (isEntity(uniqueElement) && AnnotatedDocumentId.get(uniqueElement) != null) {
				return updateInnerObject(uniqueElement, fieldName);
			}

		case UpdateWithAddSet:
			if (collection.isEmpty()) {
				skip = true;
				return null;
			}
			Object[] elements = collection.toArray();
			Object firstElement = elements[0];
			if (isEntity(firstElement)) {
				verifyNullIds(elements);
			}
			if (collection.size() == 1) {
				// { $addToSet : { field : value } } }
				BasicDBObject innerObject = new BasicDBObject(fieldName, processField(readMethod, firstElement, fieldName));
				rootDocument.put("$addToSet", innerObject);
				skip = true;
				return null;
			}
			// { $addToSet : { a : { $each : [ 3 , 5 , 6 ] } } }
			BasicDBList list = getDbList(readMethod, fieldName, collection);
			BasicDBObject eachObject = new BasicDBObject("$each", list);
			BasicDBObject innerObject = new BasicDBObject(fieldName, eachObject);
			rootDocument.put("$addToSet", innerObject);
			skip = true;
			return null;

		}
		return null;
	}

	private void verifyNullIds(Object[] elements) {
		for (int i = 0; i < elements.length; i++) {
			if (AnnotatedDocumentId.get( elements[i]) != null) {
				throw new IllegalArgumentException("Any inner Entity should not have non-null id for update actions");
			}
		}
	}

	private boolean isEnumWorkAround(Class<? extends Object> enumClass) {
		while (enumClass.isAnonymousClass()) {
			enumClass = enumClass.getSuperclass();
		}
		return enumClass.isEnum();
	}

	private BasicDBList getDbList(Method readMethod, String fieldName, Collection<?> collection) {
		BasicDBList dbList = new BasicDBList();
		for (Object object : collection) {
			dbList.add(processField(readMethod, object, fieldName));
		}
		return dbList;
	}

	private Object processEntity(Method readMethod, Object element, String fieldName) {
		DBObject innerBasicDBObject;
		if (readMethod.isAnnotationPresent(Reference.class)) {
			innerBasicDBObject = new BasicDBObject();
			innerBasicDBObject.put("_id", AnnotatedDocumentId.get(element));
			innerBasicDBObject.put("_ref", element.getClass().getCanonicalName());
		} else {
			if (operation.equals(Operation.Search)) {
				return searchForInnerObject(element, fieldName);
			} else {
				DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, element.getClass());
				innerBasicDBObject = converter.from(element).toDocument();
				innerBasicDBObject.put("_ref", element.getClass().getCanonicalName());
			}
		}
		return innerBasicDBObject;
	}

	private Object searchForInnerObject(Object element, String fieldName) {
		DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, element.getClass());
		converter.from(element).setPrefix(prefix != null ? prefix + fieldName + "." : fieldName + ".").action(operation).toDocument(rootDocument);
		this.skip = true;
		return null;
	}

	private DBObject updateInnerObject(Object element, String fieldName) {
		skip = true;
		DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter(namingStrategy, element.getClass());
		converter.from(element).setPrefix(fieldName + ".$.").toDocument(setUpdate);
		// TODO ugly!!!
		setUpdate.remove(fieldName + ".$._id");
		return null;
	}

	private BasicDBObject createSetUpdate(DBObject document) {
		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("$set", document);
		return dbObject;
	}

	@Override
	public ObjectToDocumentConverter<T> action(Operation operation) {
		this.operation = operation;
		return this;
	}

	@Override
	public ObjectToDocumentConverter<T> setPrefix(String string) {
		this.prefix = string;
		return this;
	}

}
