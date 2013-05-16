package org.monjo.core.conversion;

import org.monjo.core.annotations.Entity;
import org.monjo.document.IdentifiableDocument;

public class ConverterUtils {

	public static boolean isEntity(Object object) {
		return object instanceof IdentifiableDocument ||
				object.getClass().isAnnotationPresent(Entity.class);
	}

}
