package org.jsweet.ionicexercise.configuration.errors;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.jsweet.ionicexercise.Logger;
import org.jsweet.ionicexercise.configuration.ApplicationBeans;

/**
 * Handles specifically one type of exception
 * 
 * @author lgrignon
 */
public abstract class AbstractExceptionsHandler extends ExceptionHandlerWrapper {

	/**
	 * Defines exception handling status
	 * 
	 * @author lgrignon
	 * 
	 */
	public enum HandlingStatus {
		/**
		 * Handled exception
		 */
		Handled,

		/**
		 * Ignored exception
		 */
		Ignored,

		/**
		 * Error during exception handling
		 */
		Error
	}

	protected final Logger log = Logger.getLogger(getClass());

	private ExceptionHandler wrapped;
	private ApplicationBeans applicationBeans;
	private Collection<Class<? extends Throwable>> handledExceptionTypes;

	protected AbstractExceptionsHandler(ExceptionHandler wrapped,
			Collection<Class<? extends Throwable>> handledExceptionTypes) {
		this.wrapped = wrapped;
		this.handledExceptionTypes = handledExceptionTypes;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			Throwable exception = context.getException();
			if (this.handle(exception) == HandlingStatus.Handled) {
				i.remove();
			}
		}

		getWrapped().handle();
	}

	protected HandlingStatus handle(Throwable exception) {
		HandlingStatus status = HandlingStatus.Ignored;
		Throwable actualExceptionToHandle = this.getActualExceptionToHandle(exception);
		log.trace("%s can handle %s: %s (Handled exception types: %s)", this.getClass().getSimpleName(), exception,
				actualExceptionToHandle != null, this.handledExceptionTypes);

		if (actualExceptionToHandle != null) {
			log.error("%s handling following error", actualExceptionToHandle, this.getClass().getSimpleName());
			try {
				if (this.doHandle(actualExceptionToHandle)) {
					status = HandlingStatus.Handled;
				}
			} catch (Exception e) {
				log.error("Error during exception handling !!", e);
				status = HandlingStatus.Error;
			}
		}

		return status;
	}

	protected <TBean> TBean getBean(Class<TBean> beanType, Annotation... annotations) {
		if (applicationBeans == null) {
			applicationBeans = ApplicationBeans.instance();
		}

		return applicationBeans.getBean(beanType, annotations);
	}

	private Throwable getActualExceptionToHandle(Throwable exception) {
		for (final Class<? extends Throwable> handledExceptionType : this.handledExceptionTypes) {
			Throwable current = exception;
			do {
				if (handledExceptionType.isAssignableFrom(current.getClass())) {
					return current;
				}
			} while ((current = current.getCause()) != null);
		}

		return null;
	}

	protected abstract boolean doHandle(Throwable exception);
}
