package org.monjo.core.conversion;

import contrib.org.hibernate.cfg.DefaultNamingStrategy;
import contrib.org.hibernate.cfg.NamingStrategy;

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
		return new DefaultObjectToDocumentConverter<T>(namingStrategy, class1);
	}
	
	public <T> DocumentToObjectConverter<T> getDefaultDocumentConverter(Class<T> class1){
		return new DefaultDocumentToObjectConverter<T>(namingStrategy, class1);
	}
	
	public <T> MonjoConverter<T> getDefaultMonjoConverter(Class<T> class1){
		return new DefaultMonjoConverter<T>(namingStrategy, class1);	
	}
	
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}
	
}

