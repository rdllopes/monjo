package org.pojongo.core.conversion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used for marking transient fields, i.e., fields that<br />
 * should not be converted to or from MongoDB documents.<br /><br />
 * 
 * Usage is as simple as annotating the field with <code>@Transient</code>:<br /><br />
 * 
 * <code>@Transient private String myTransientField;</code>
 * 
 * @author Caio Filipini
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
}
