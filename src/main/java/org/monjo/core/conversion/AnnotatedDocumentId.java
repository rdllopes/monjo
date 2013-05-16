package org.monjo.core.conversion;

import java.lang.reflect.Method;

import org.monjo.core.annotations.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author marcos
 * 
 */
public class AnnotatedDocumentId {

	private static final Logger logger = LoggerFactory.getLogger(AnnotatedDocumentId.class);
	
	public static Object get(Object document) {
		Method[] methods;
		if (document.getClass().getName().contains("CGLIB")) { // FIXME como fazer isso direito?
			methods = document.getClass().getSuperclass().getMethods();
		} else {
			methods = document.getClass().getMethods();
		}
		for (Method method : methods) {
			if (method.isAnnotationPresent(Id.class) && method.getName().startsWith("get")) {
				try {
					return method.invoke(document);
				} catch (Exception e) {
					logger.error("fail in {} using {}.", method.getName(), document);
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}

	public static void set(Object document, Object id) {
		Method[] methods;
		if (document.getClass().getName().contains("CGLIB")) { // FIXME como fazer isso direito?
			methods = document.getClass().getSuperclass().getMethods();
		} else {
			methods = document.getClass().getMethods();
		}
		for (Method method : methods) {
			if (method.isAnnotationPresent(Id.class) && method.getName().startsWith("set")) {
				try {
					method.invoke(document, id);
				} catch (Exception e) {
					logger.error("fail in {} using {}.", method.getName(), document);
					throw new RuntimeException(e);
				}
			}
		}
	}

}
