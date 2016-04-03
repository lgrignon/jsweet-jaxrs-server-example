package org.jsweet.ionicexercise;

import java.io.Serializable;

public class Logger implements Serializable {

	private static final long serialVersionUID = 1L;

	private Class<?> clazz;

	private transient org.slf4j.Logger delegateLogger;

	public static Logger getLogger(Class<?> clazz) {
		return new Logger(clazz);
	}
	
	/**
	 * Builds logger for given class
	 * 
	 * @param name
	 *            Logger name
	 */
	public Logger(Class<?> clazz) {
		this.clazz = clazz;
	}

	public boolean isTraceEnabled() {
		return getDelegateLogger().isTraceEnabled();
	}

	public boolean isDebugEnabled() {
		return getDelegateLogger().isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return getDelegateLogger().isInfoEnabled();
	}

	public boolean isWarnEnabled() {
		return getDelegateLogger().isWarnEnabled();
	}

	public boolean isErrorEnabled() {
		return getDelegateLogger().isErrorEnabled();
	}

	public void trace(Object messageFormat, Object... args) {
		getDelegateLogger().trace(getMessage(messageFormat, args));
	}

	public void trace(Object messageFormat, Throwable t) {
		getDelegateLogger().trace(getMessage(messageFormat), t);
	}

	public void debug(Object messageFormat, Object... args) {
		getDelegateLogger().debug(getMessage(messageFormat, args));
	}

	public void debug(Object messageFormat, Throwable t) {
		getDelegateLogger().debug(getMessage(messageFormat), t);
	}

	public void warn(Object messageFormat, Object... args) {
		getDelegateLogger().warn(getMessage(messageFormat, args));
	}

	public void warn(Object messageFormat, Throwable t) {
		getDelegateLogger().warn(getMessage(messageFormat), t);
	}

	public void error(Object messageFormat, Object... args) {
		getDelegateLogger().error(getMessage(messageFormat, args));
	}

	public void error(final Object messageFormat, final Throwable t, final Object... args) {
		getDelegateLogger().error(getMessage(messageFormat, args), t);
	}

	public void info(Object messageFormat, Object... args) {
		getDelegateLogger().info(getMessage(messageFormat, args));
	}

	public void info(Object messageFormat, Throwable t) {
		getDelegateLogger().info(getMessage(messageFormat), t);
	}

	private String getMessage(Object message, Object... args) {
		return message == null ? "null" : String.format(message.toString(), args);
	}

	private org.slf4j.Logger getDelegateLogger() {
		if (this.delegateLogger == null) {
			this.delegateLogger = org.slf4j.LoggerFactory.getLogger(this.clazz);
		}

		return delegateLogger;
	}
}
