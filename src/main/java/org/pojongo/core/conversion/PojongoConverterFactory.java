package org.pojongo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class PojongoConverterFactory {
	
	private NamingStrategy namingStrategy;

	private static PojongoConverterFactory converterFactory 
		= new PojongoConverterFactory();
	
	/**
	 * Singleton
	 * @return
	 */
	public static PojongoConverterFactory getInstance(){
		return converterFactory;
	}

	public PojongoConverterFactory configure(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		return this;
	}

	private PojongoConverterFactory(){
		this(new DefaultNamingStrategy());
	}

	private PojongoConverterFactory(
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
	
	public DocumentToObjectConverter getDefaultObjectConverter(){
		DefaultDocumentToObjectConverter converter = new DefaultDocumentToObjectConverter();
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
	}
	
	public PojongoConverter getDefaultPojongoConverter(){
		PojongoConverter converter = new DefaultPojongoConverter();
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
		
	}
	
}

