package org.pojongo.core.conversion;

import com.mongodb.DBObject;

/**
 * Interface defining methods to convert MongoDB documents into plain Java objects.<br /><br />
 * 
 * Classes implementing this interface must be responsible for converting <code>DBObject</code><br />
 * instances into instances of a given Java Class based on the matching fields between them.
 * 
 * @author Caio Filipini
 */
public interface DocumentToObjectConverter {

	/**
	 * Configures which <code>DBObject</code> should be converted.<br /><br />
	 * 
	 * This method returns the converter itself, so it can be used as a fluent interface<br />
	 * for converting the objects. For example:<br /><br />
	 * 
	 * <code>MyClass myObject = converter.from(someDocument).to(MyClass.class);</code>
	 * 
	 * @param document the <code>DBObject</code> to be converted to Java object.
	 * @return the converter.
	 * @throws IllegalArgumentException if <code>document</code> is null.
	 */
	DocumentToObjectConverter from(final DBObject document) throws IllegalArgumentException;

	/**
	 * Converts the previously configured <code>DBObject</code> to a corresponding instance<br />
	 * of the specified Java class <code>objectType</code>.<br /><br/>
	 * 
	 * The conversion is done by reflecting <code>objectType</code>'s attributes and finding<br />
	 * corresponding fields in <code>DBObject</code>. If a matching field is found, its value is<br />
	 * set on the target object, preserving the data type returned by MongoDB's driver.
	 * 
	 * @param <T> the generic type for objectType.
	 * @param objectType the type to be converted to.
	 * @return an instance of <code>objectType</code> populated with corresponding values from MongoDB's document.
	 * 
	 * @throws IllegalStateException if <code>to(Class)</code> is called without calling <code>from(DBObject)</code> first.
	 * @throws IllegalArgumentException if <code>objectType</code> is <code>null</code>.
	 */
	<T extends Object> T to(final Class<T> objectType);

}