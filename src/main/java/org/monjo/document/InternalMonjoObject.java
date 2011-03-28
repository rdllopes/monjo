package org.monjo.document;

import java.util.Set;

/*
 * FIXME isso deveria extender IdentifiableDocument...
 */
public interface InternalMonjoObject{

	public Set<String> dirtFields();

	public void addDirtField(String fieldName);
}
