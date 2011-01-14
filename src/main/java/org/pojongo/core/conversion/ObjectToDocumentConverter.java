package org.pojongo.core.conversion;

import com.mongodb.DBObject;

/**
 * Interface defining methods to convert Java objects into MongoDB documents.<br /><br />
 * 
 * Classes implementing this interface must be responsible for converting <code>Object</code><br />
 * instances into instances of MongoDB's <code>DBObject</code> based on the matching fields between them.
 * 
 * @author Caio Filipini
 */
public interface ObjectToDocumentConverter {

	/**
	 * Configures which <code>Object</code> should be converted.<br /><br />
	 * 
	 * This method returns the converter itself, so it can be used as a fluent interface<br />
	 * for converting the objects. For example:<br /><br />
	 * 
	 * <code>DBObject document = converter.from(someObject).toDocument();</code>
	 * 
	 * @param object the <code>Object</code> to be converted to <code>DBObject</code>.
	 * @return the converter.
	 * @throws IllegalArgumentException if <code>object</code> is null.
	 */
	ObjectToDocumentConverter from(Object object);

	/**
	 * Converts the previously configured <code>Object</code> into a corresponding MongoDB's<br />
	 * <code>DBObject</code> instance.<br /><br/>
	 * 
	 * The conversion is done by reflecting <code>object</code>'s attributes.<br />
	 * For each attribute found, if its value is not <code>null</code>, then that value is populated<br />
	 * into a field with the exact same name in the <code>DBObject</code> instance.<br /><br />
	 * 
	 * An important thing to mention is that if an attribute named <code>id</code> is found in <code>object</code>,<br />
	 * then its value is populated into the corresponding special <code>_id</code> field in MongoDB's <code>DBObject</code>.
	 * 
	 * @return an instance of <code>DBObject</code> populated with corresponding values from the specified <code>object</code>.
	 * @throws IllegalStateException if <code>toDocument()</code> is called without calling <code>from(Object)</code> first.
	 */
	DBObject toDocument();

	ObjectToDocumentConverter enableUpdate();

}
