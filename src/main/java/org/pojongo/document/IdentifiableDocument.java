package org.pojongo.document;

/**
 * Interface defining an identifiable MongoDB document.
 * 
 * When your POJO class implements this interface, Pojongo assumes that an attribute<br />
 * named <em>id</em> exists in that class, and populates it with the value held by the<br />
 * <em>_id</em> field from MongoDB's document.
 * 
 * @author Caio Filipini
 *
 * @param <T> the type of the document id.
 */
public interface IdentifiableDocument<T> {
	
	/**
	 * Returns the document id.
	 * @return the document id.
	 */
	public T getId();
	
	/**
	 * Modifier for property id
	 * @param id
	 */
	public void setId(T id);

}
