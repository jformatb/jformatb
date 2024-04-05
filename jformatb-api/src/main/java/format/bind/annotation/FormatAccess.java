/*
 * Copyright 2024 jFormat-B
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package format.bind.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to specify how to access text format fields.
 * 
 * @author Yannick Ebongue
 */
@Documented
@Inherited
@Retention(RUNTIME)
@Target(TYPE)
public @interface FormatAccess {

	/**
	 * (Required) Specification of field- or property-based access.
	 * 
	 * @return The access type value.
	 */
	Type value();

	/**
	 * Used with the {@link FormatAccess} annotation to specify an access type to be applied.
	 * 
	 * @author Yannick Ebongue
	 */
	enum Type {

		/** Field-based access is used. */
		FIELD,

		/** Property-based access is used. */
		PROPERTY

	}

}
