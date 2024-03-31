/*
* Copyright (c) 2019 by Diebold Nixdorf
* This software is the confidential and proprietary information of Diebold Nixdorf.
*/
/**
 * 
 */
package format.bind;

/**
 * The root class for all Format Binding exceptions.
 * 
 * @author Yannick Ebongue
 */
public abstract class FormatException extends RuntimeException {

	private static final long serialVersionUID = 6931624954691907185L;

	/**
	 * Creates a new {@code FormatException} with the specified detail message.
	 * 
	 * @param message The detail message.
	 */
	public FormatException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@code FormatException} with the specified detail message and cause.
	 * 
	 * @param message The detail message.
	 * @param cause The cause.
	 */
	public FormatException(String message, Throwable cause) {
		super(message, cause);
	}

}
