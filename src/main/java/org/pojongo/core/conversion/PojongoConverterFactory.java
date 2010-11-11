package org.pojongo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class PojongoConverterFactory {
	
	private NamingStrategy namingStrategy;

	private static PojongoConverterFactory converterFactory 
		= new PojongoConverterFactory();
	
	/**
	 * @return the singleton instance of PojongoConverterFactory
	 */
	public static PojongoConverterFactory getInstance(){
		return converterFactory;
	}
	
	/**
	 * Set NamingStrategy to this Factory
	 * @param namingStrategy
	 * @return
	 */
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
	
	public ObjectToDocumentConverter getDefaultObjectConverter(){
		DefaultObjectToDocumentConverter converter = new DefaultObjectToDocumentConverter();
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
	}
	
	public DocumentToObjectConverter getDefaultDocumentConverter(){
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
	
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}
	
}

