package org.monjo.document;

import java.util.Set;

/*
 * FIXME isso deveria extender IdentifiableDocument...
 */
public interface DirtFieldsWatcher{

	public Set<String> dirtFields();

	public void addDirtField(String fieldName);
}
