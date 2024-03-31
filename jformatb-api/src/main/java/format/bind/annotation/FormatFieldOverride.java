/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to override text format fields mapped on a superclass or JavaBean properties
 * annotated with {@link FormatFieldContainer}.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatFieldContainer
 * @see FormatSubTypes
 * @see FormatTypeInfo
 * 
 */
@Documented
@Repeatable(FormatFieldOverrides.class)
@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
public @interface FormatFieldOverride {

	/**
	 * (Required) The name of the property whose mapping is being overridden.
	 * 
	 * @return The name of the property whose mapping is being overridden.
	 */
	String property();

	/**
	 * (Required) The {@link FormatField} annotation with specification to apply.
	 * 
	 * @return The {@link FormatField} annotation with specification to apply.
	 */
	FormatField field();

}
