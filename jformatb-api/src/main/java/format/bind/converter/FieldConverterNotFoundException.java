/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
package format.bind.converter;

import format.bind.FormatException;
import format.bind.converter.spi.FieldConverterProvider;

/**
 * Thrown by the {@link FieldConverterProvider} when there is no field converter for
 * a specified field Java type.
 * 
 * @author Yannick Ebongue
 */
public class FieldConverterNotFoundException extends FormatException {

	private static final long serialVersionUID = -7062317058328977232L;

	/**
	 * Creates a new {@code FieldConverterNotFoundException} with the specified
	 * detail message.
	 * 
	 * @param message The detail message.
	 */
	public FieldConverterNotFoundException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code FieldConverterNotFoundException} with the specified
	 * detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public FieldConverterNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
