package org.monjo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class MonjoConverterFactory {
	
	private NamingStrategy namingStrategy;

	private static MonjoConverterFactory converterFactory 
		= new MonjoConverterFactory();
	
	/**
	 * @return the singleton instance of PojongoConverterFactory
	 */
	public static MonjoConverterFactory getInstance(){
		return converterFactory;
	}
	
	/**
	 * Set NamingStrategy to this Factory
	 * @param namingStrategy
	 * @return
	 */
	public MonjoConverterFactory configure(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		return this;
	}

	private MonjoConverterFactory(){
		this(new DefaultNamingStrategy());
	}

	private MonjoConverterFactory(
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
	
	public MonjoConverter getDefaultPojongoConverter(){
		MonjoConverter converter = new DefaultPojongoConverter();
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
		
	}
	
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}
	
}

