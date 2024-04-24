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
package format.bind.runtime.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;

import format.bind.FormatFieldAccessor;
import format.bind.FormatFieldAccessor.Strategy;
import lombok.experimental.UtilityClass;

/**
 * An utility class that provides field accessors based on target type access strategy.
 * 
 * @author Yannick Ebongue
 * 
 */
@UtilityClass
class FormatFieldAccessorUtil {

	/**
	 * The map of functions for building field accessors of a given type.
	 */
	private Map<Strategy, Function<Class<?>, List<FormatFieldAccessor>>> builders = new EnumMap<>(Strategy.class);

	/**
	 * The cache of the generated field accessor list by types.
	 */
	private ConcurrentMap<Class<?>, List<FormatFieldAccessor>> accessors = new ConcurrentHashMap<>();

	static {
		builders.put(Strategy.FIELD, FormatFieldAccessorUtil::buildDefaultFieldAccessorList);
		builders.put(Strategy.PROPERTY, FormatFieldAccessorUtil::buildPropertyFieldAccessorList);
	}

	/**
	 * Builds the default field accessor list of the given type.
	 * 
	 * <p>
	 * This method collects all non static fields of the given type parameter to get
	 * the field accessor of.
	 * </p>
	 * 
	 * @param type The type to be processed.
	 * @return The list of field accessors.
	 */
	private List<FormatFieldAccessor> buildDefaultFieldAccessorList(final Class<?> type) {
		return FieldUtils.getAllFieldsList(type).stream()
				.filter(field -> !(Modifier.isStatic(field.getModifiers())))
				.map(FormatFieldAccessorImpl.Default::of)
				.collect(Collectors.toList());
	}

	/**
	 * Builds the property field accessor list of the given type.
	 * 
	 * <p>
	 * This method collects all JavaBeans properties of the given type parameter to
	 * get the field accessor of.
	 * </p>
	 * 
	 * @param type The type to be processed.
	 * @return The list of field accessors.
	 */
	private List<FormatFieldAccessor> buildPropertyFieldAccessorList(final Class<?> type) {
		return Arrays.stream(BeanUtilsBean.getInstance().getPropertyUtils().getPropertyDescriptors(type))
				.map(FormatFieldAccessorImpl.Property::of)
				.collect(Collectors.toList());
	}

	/**
	 * Lookup for the registered field accessors of the given bean type.
	 * 
	 * @param <T> The type to look for.
	 * @param beanType The class instance representing the type to process.
	 * @return The list of field accessors.
	 */
	private <T> List<FormatFieldAccessor> findFieldAccessors(final Class<? super T> beanType) {
		return Optional.ofNullable(beanType)
				.map(type -> Optional.ofNullable(accessors.get(type))
						.orElse(findFieldAccessors(type.getSuperclass())))
				.orElseGet(Collections::emptyList);
	}

	/**
	 * Get the field accessor by name of the given type.
	 * 
	 * @param beanType The type to be processed.
	 * @param name The name of the field accessor.
	 * @return The field accessor.
	 */
	FormatFieldAccessor getFieldAccessor(final Class<?> beanType, final String name) {
		return findFieldAccessors(beanType).stream()
				.filter(accessor -> accessor.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Unable to find property '%s' in bean class %s", name, beanType)));
	}

	/**
	 * Get a list of field accessors filtered the given annotation type that should
	 * be present on the field accessors to return.
	 * 
	 * @param <A> The annotation type.
	 * @param strategy The strategy used to find field accessors.
	 * @param beanType The type to be processed.
	 * @param annotationType The annotation type instance to filter the field
	 * 		accessor list.
	 * @return The filtered list of field accessors.
	 */
	<A extends Annotation> List<FormatFieldAccessor> getFieldAccessors(final Strategy strategy, final Class<?> beanType, final Class<A> annotationType) {
		return accessors.computeIfAbsent(beanType, type -> builders.get(strategy).apply(type)).stream()
				.filter(accessor -> accessor.isAnnotationPresent(annotationType))
				.collect(Collectors.toList());
	}

}
