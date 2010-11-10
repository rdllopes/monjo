package org.pojongo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class ObjectToDocumentConverterFactory {
	
	private NamingStrategy namingStrategy;

	public ObjectToDocumentConverterFactory(){
		this(new DefaultNamingStrategy());
	}

	public ObjectToDocumentConverterFactory(
			NamingStrategy defaultNamingStrategy) {
		this.namingStrategy = defaultNamingStrategy;
	}
	
	public ObjectToDocumentConverter getDefaultDocumentConverter(){
		DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter();
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
	}
}

