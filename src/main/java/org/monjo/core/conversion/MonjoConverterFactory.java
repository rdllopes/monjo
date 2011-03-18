package org.monjo.core.conversion;

import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class MonjoConverterFactory {
	
	private NamingStrategy namingStrategy;

	private static MonjoConverterFactory converterFactory 
		= new MonjoConverterFactory();
	
	/**
	 * @return the singleton instance of MonjoConverterFactory
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
	
	public <T> ObjectToDocumentConverter<T> getDefaultObjectConverter(Class<T> class1){
		DefaultObjectToDocumentConverter<T> converter = new DefaultObjectToDocumentConverter<T>(class1);
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
	}
	
	public <T> DocumentToObjectConverter<T> getDefaultDocumentConverter(Class<T> class1){
		DefaultDocumentToObjectConverter<T> converter = new DefaultDocumentToObjectConverter<T>(class1);
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
	}
	
	public <T> MonjoConverter<T> getDefaultMonjoConverter(Class<T> class1){
		MonjoConverter<T> converter = new DefaultMonjoConverter<T>(class1);
		if (namingStrategy != null){
			converter.setNamingStrategy(namingStrategy);	
		}
		return converter;
		
	}
	
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}
	
}

