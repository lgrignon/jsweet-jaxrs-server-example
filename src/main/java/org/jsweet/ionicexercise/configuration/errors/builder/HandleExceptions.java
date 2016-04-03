package org.jsweet.ionicexercise.configuration.errors.builder;

import java.util.Arrays;
import java.util.Collection;

import org.jsweet.ionicexercise.configuration.ApplicationBeans;
import org.jsweet.ionicexercise.configuration.errors.ICustomExceptionHandler;

/**
 * Choose how to handle application exception
 * 
 * @author lgrignon
 * 
 */
public class HandleExceptions {

	/**
	 * Handle exceptions of given types with ...
	 * 
	 * @param exceptionTypes
	 *            Handled exception type
	 * 
	 * @return Builder
	 */
	@SafeVarargs
	public static ExceptionHandlersBuilder ofTypes(Class<? extends Throwable>... exceptionTypes) {
		return new ExceptionHandlersBuilder(Arrays.asList(exceptionTypes));
	}

	/**
	 * Handle exceptions with...
	 * 
	 * @author lgrignon
	 * 
	 */
	public static class ExceptionHandlersBuilder extends AbstractHandlerBuilder {

		private Collection<Class<? extends Throwable>> exceptionTypes;

		protected ExceptionHandlersBuilder(Collection<Class<? extends Throwable>> exceptionTypes) {
			setExceptionTypes(exceptionTypes);
		}

		protected ExceptionHandlersBuilder(AbstractHandlerBuilder parentHandlerBuilder,
				Collection<Class<? extends Throwable>> exceptionTypes) {
			super(parentHandlerBuilder);
			setExceptionTypes(exceptionTypes);
		}

		/**
		 * Handles exception with redirection to specified view id
		 * 
		 * @param <TException>
		 *            Handled exception type
		 * @param <THandler>
		 *            Custom handler type
		 * @param handlerType
		 *            Exception handler type
		 * 
		 * @return Builder for redirection handling
		 */
		// Cast Exception is a good thing here !
		@SuppressWarnings("unchecked")
		public <TException extends Throwable, THandler extends ICustomExceptionHandler<TException>> CustomHandlerBuilder<TException> withCustomHandler(
				Class<THandler> handlerType) {
			if (exceptionTypes.size() > 1) {
				throw new UnsupportedOperationException(String.format(
						"Cannot define user defined exception handler on more than ONE exception type (Exception Types:%s)",
						exceptionTypes));
			}

			THandler handler = ApplicationBeans.get(handlerType);

			return new CustomHandlerBuilder<TException>(this.parentBuilder,
					(Class<TException>) exceptionTypes.iterator().next(), handler);
		}

		// Cast Exception is a good thing here !
		@SuppressWarnings("unchecked")
		public <TException extends Throwable, THandler extends ICustomExceptionHandler<TException>> CustomHandlerBuilder<TException> withCustomHandler(
				THandler handler) {
			if (exceptionTypes.size() > 1) {
				throw new UnsupportedOperationException(String.format(
						"Cannot define user defined exception handler on more than ONE exception type (Exception Types:%s)",
						exceptionTypes));
			}

			return new CustomHandlerBuilder<TException>(this.parentBuilder,
					(Class<TException>) exceptionTypes.iterator().next(), handler);
		}

		/**
		 * Handles exception by throwing another one
		 * 
		 * @return Builder for exception handling
		 */
		public ThrowAnotherExceptionHandlerBuilder withAnotherException() {
			return new ThrowAnotherExceptionHandlerBuilder(this.parentBuilder, exceptionTypes);
		}

		private void setExceptionTypes(Collection<Class<? extends Throwable>> exceptionTypes) {
			if (exceptionTypes.isEmpty()) {
				throw new IllegalArgumentException("Handled exception types cant be empty");
			}

			this.exceptionTypes = exceptionTypes;
		}
	}
}
