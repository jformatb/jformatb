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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import format.bind.FormatFieldAccessor;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.NONE)
abstract class FormatFieldAccessorImpl implements FormatFieldAccessor {

	/**
	 * The default format field accessor.
	 * 
	 * @author Yannick Ebongue
	 */
	@Value(staticConstructor = "of")
	@EqualsAndHashCode(callSuper = false)
	static class Default extends FormatFieldAccessorImpl {

		/**
		 * The field used to access format field property.
		 */
		private Field field;

		@Override
		public String getName() {
			return field.getName();
		}

		@Override
		public Class<?> getType() {
			return field.getType();
		}

		@Override
		public Type getGenericType() {
			return field.getGenericType();
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return field.getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations() {
			return field.getAnnotations();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return field.getDeclaredAnnotations();
		}

		@Override
		public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
			return field.getAnnotationsByType(annotationClass);
		}

		@Override
		public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
			return field.getDeclaredAnnotation(annotationClass);
		}

		@Override
		public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
			return field.getDeclaredAnnotationsByType(annotationClass);
		}

	}

	/**
	 * The property format field accessor.
	 */
	@Value(staticConstructor = "of")
	@EqualsAndHashCode(callSuper = false)
	static class Property extends FormatFieldAccessorImpl {

		/**
		 * The property descriptor used to access format field property.
		 */
		private PropertyDescriptor descriptor;

		@Override
		public String getName() {
			return descriptor.getName();
		}

		@Override
		public Class<?> getType() {
			return descriptor.getReadMethod().getReturnType();
		}

		@Override
		public Type getGenericType() {
			return descriptor.getReadMethod().getGenericReturnType();
		}

		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return descriptor.getReadMethod().getAnnotation(annotationClass);
		}

		@Override
		public Annotation[] getAnnotations() {
			return descriptor.getReadMethod().getAnnotations();
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return descriptor.getReadMethod().getDeclaredAnnotations();
		}

		@Override
		public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
			return descriptor.getReadMethod().getAnnotationsByType(annotationClass);
		}

		@Override
		public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
			return descriptor.getReadMethod().getDeclaredAnnotation(annotationClass);
		}

		@Override
		public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
			return descriptor.getReadMethod().getDeclaredAnnotationsByType(annotationClass);
		}

	}

}
