package org.jsweet.ionicexercise.configuration;

/**
 * Exception thrown when a bad configuration has been detected
 * 
 * @author lgrignon
 * @version 1.0.0
 */
public class ConfigurationException extends RuntimeException {

	private final static long serialVersionUID = 0L;

	/**
	 * Builds a new <tt>ConfigurationException</tt> without informations
	 */
	public ConfigurationException() {
		super();
	}

	/**
	 * Builds a new <tt>ConfigurationException</tt> with an error message
	 * 
	 * @param message
	 *            Error message
	 * @param formatParams
	 *            Message format params
	 */
	public ConfigurationException(final String message, final Object... formatParams) {
		super(String.format(message, formatParams));
	}

	/**
	 * Builds a new <tt>ConfigurationException</tt> with a source exception
	 * 
	 * @param cause
	 *            Source exception
	 */
	public ConfigurationException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Builds a new <tt>ConfigurationException</tt> with an error message and a
	 * source exception
	 * 
	 * @param message
	 *            Error message
	 * @param cause
	 *            Source exception
	 */
	public ConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
