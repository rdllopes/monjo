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
		Method[] methods = getMethods(document);
		Method method = getIdMethod(methods);
		if (method == null){
			return null;
		}
		try {
			return method.invoke(document);
		} catch (Exception e) {
			logger.error("fail in {} using {}.", method.getName(), document);
			throw new RuntimeException(e);
		}		
	}

	private static Method[] getMethods(Object document) {
		Method[] methods;
		if (document.getClass().getName().contains("CGLIB")) { // FIXME como fazer isso direito?
			methods = document.getClass().getSuperclass().getMethods();
		} else {
			methods = document.getClass().getMethods();
		}
		return methods;
	}

	private static Method getIdMethod(Method[] methods) {
		Method getIdMethod = null;
		for (Method method : methods) {
			if (method.isAnnotationPresent(Id.class) && method.getName().startsWith("get")) {
				return method;
			}
			if ("getId".equals(method.getName())){
				getIdMethod = method;
			}
		}
		return getIdMethod;
	}

	private static Method setIdMethod(Method[] methods) {
		Method getIdMethod = null;
		for (Method method : methods) {
			if (method.isAnnotationPresent(Id.class) && method.getName().startsWith("set")) {
				return method;
			}
			if ("setId".equals(method.getName())){
				getIdMethod = method;
			}
		}
		return getIdMethod;
	}
	
	public static void set(Object document, Object id) {
		Method method = setIdMethod(getMethods(document));
		if (method == null){
			return ;
		}
		try {
			method.invoke(document, id);
		} catch (Exception e) {
			logger.error("fail in {} using {}.", method.getName(), document);
			throw new RuntimeException(e);
		}		
	}

}
