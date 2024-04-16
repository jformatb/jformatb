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
package format.bind;

import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import format.bind.converter.spi.FieldConverterProvider;
import format.bind.spi.FormatProcessorFactory;

/**
 * Utility class used to obtain jFormat API service provider interfaces.
 * 
 * @author Yannick Ebongue
 * 
 * @see FormatProcessorFactory
 * @see FieldConverterProvider
 */
public class Providers {

	/** The singleton instance of this class. */
	private static Providers instance;

	/** The cache of resolved SPIs. */
	private Cache cache;

	/**
	 * Creates a new instance of this class.
	 */
	private Providers() {
		cache = new Cache();
	}

	/**
	 * Get the singleton instance if this class.
	 * 
	 * @return The singleton instance of this class.
	 */
	public static synchronized Providers getInstance() {
		if (instance == null) {
			instance = new Providers();
		}

		return instance;
	}

	/**
	 * Obtain the {@link FormatProcessorFactory} SPI implementation.
	 * 
	 * @return The {@link FormatProcessorFactory} SPI implementation.
	 */
	public FormatProcessorFactory getProcessorFactory() {
		return getInstance().get(FormatProcessorFactory.class);
	}

	/**
	 * Obtain the {@link FieldConverterProvider} SPI implementation.
	 * 
	 * @return The {@link FieldConverterProvider} SPI implementation.
	 */
	public FieldConverterProvider getFieldConverterProvider() {
		return getInstance().get(FieldConverterProvider.class);
	}

	/**
	 * Obtain the available SPI implementation of the given interface.
	 * 
	 * @param <T> The type of the SPI.
	 * @param serviceProviderInterface The interface instance of the SPI implementation.
	 * @return The SPI implementation.
	 * @throws FormatException if no SPT implementation found.
	 */
	@SuppressWarnings("unchecked")
	private <T> T get(Class<T> serviceProviderInterface) {
		String interfaceName = serviceProviderInterface.getName();

		if (cache.containsKey(interfaceName)) {
			return (T) cache.get(interfaceName);
		} else {
			ServiceLoader<T> loader = ServiceLoader.load(serviceProviderInterface);
			Iterator<T> it = loader.iterator();

			String className = System.getProperty(interfaceName);
			T found = null;

			while (it.hasNext()) {
				T next = it.next();

				if (found == null) {
					found = next;
				}

				if (next.getClass().getName().equals(className)) {
					found = next;
					break;
				}
			}

			Optional<T> provider = Optional.ofNullable(found);

			provider.ifPresent(value -> cache.put(interfaceName, value));

			return provider.orElseThrow(() -> new FormatException(String.format("No provider found for '%s' interface.", interfaceName)));
		}
	}

	/**
	 * A simple cache containing the resolved SPI implementations
	 * 
	 * @author Yannick Ebongue
	 */
	private static class Cache {

		/** The concurrent map of resolved SPI implementations. */
		private ConcurrentMap<String, Object> items = new ConcurrentHashMap<>();

		/**
		 * Check if a SPI implementation associated with the given key exist.
		 * @param key The key associated to a SPI implementation to check.
		 * @return {@code true} if the {@code key} exist. Otherwise return {@code false}.
		 */
		public boolean containsKey(String key) {
			return items.containsKey(key);
		}

		/**
		 * Return the SPI implementation associated with the given key.
		 * @param key The key associated to the SPI implementation.
		 * @return The SPI implementation or {@code null} if not exist.
		 */
		public Object get(String key) {
			return items.get(key);
		}

		/**
		 * Associate the SPI implementation with the given key.
		 * @param key The key to associate with the SPI implementation.
		 * @param value The SPI implementation to associate.
		 * @return The associated SPI implementation.
		 */
		public Object put(String key, Object value) {
			return items.put(key, value);
		}

	}

}
