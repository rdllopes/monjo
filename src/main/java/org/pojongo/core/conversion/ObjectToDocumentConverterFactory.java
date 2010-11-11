package org.pojongo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class ObjectToDocumentConverterFactory {
	
	private NamingStrategy namingStrategy;

	private static ObjectToDocumentConverterFactory converterFactory 
		= new ObjectToDocumentConverterFactory();
	
	/**
	 * Singleton
	 * @return
	 */
	public static ObjectToDocumentConverterFactory getInstance(){
		return converterFactory;
	}

	public ObjectToDocumentConverterFactory configure(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		return this;
	}

	private ObjectToDocumentConverterFactory(){
		this(new DefaultNamingStrategy());
	}

	private ObjectToDocumentConverterFactory(
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

