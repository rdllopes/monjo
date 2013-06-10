package org.monjo.core.conversion;

import static org.monjo.core.conversion.ConverterUtils.isEntity;

import org.monjo.core.Operation;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import contrib.org.hibernate.cfg.NamingStrategy;

public class DefaultMonjoConverter<T extends Object> implements MonjoConverter<T> {

	public DefaultMonjoConverter(NamingStrategy namingStrategy, Class<T> objectType) {
		documentToObjectConverter = new DefaultDocumentToObjectConverter<T>(namingStrategy,objectType);
		objectToDocumentConverter = new DefaultObjectToDocumentConverter<T>(namingStrategy, objectType);
	}

	private final DefaultDocumentToObjectConverter<T> documentToObjectConverter;
	private final DefaultObjectToDocumentConverter<T> objectToDocumentConverter;
	private String prefix;

	@Override
	public DefaultDocumentToObjectConverter<T> from(DBObject document) {
		return documentToObjectConverter.from(document);
	}

	@Override
	public T to() {
		return documentToObjectConverter.to();
	}

	@Override
	public ObjectToDocumentConverter<T> from(T javaObject) {
		objectToDocumentConverter.from(javaObject);
		return this;
	}

	@Override
	public DBObject toDocument() {
		return objectToDocumentConverter.toDocument();
	}

	@Override
	@SuppressWarnings("unchecked")
	public DBObject getIdDocument(Object object) {
		if (isEntity(object)) {
			DBObject dbObject = new BasicDBObject();
			dbObject.put((prefix == null) ? "_id" : prefix + "._id", AnnotatedDocumentId.get(object));
			return dbObject;
		}
		return null;

	}

	@Override
	public ObjectToDocumentConverter<T> setPrefix(String string) {
		objectToDocumentConverter.setPrefix(string);
		this.prefix = string;
		return this;
	}

	@Override
	public DBObject toDocument(BasicDBObject document) {
		return objectToDocumentConverter.toDocument(document);
	}

	@Override
	public ObjectToDocumentConverter<T> action(Operation operation) {
		objectToDocumentConverter.action(operation);
		return this;

	}

}
