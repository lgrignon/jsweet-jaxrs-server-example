package org.jsweet.ionicexercise.configuration.errors;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.ExceptionHandler;

/**
 * Handles specifically one type of exception
 * 
 * @author lgrignon
 * 
 * @param <TException>
 */
public abstract class AbstractSpecificExceptionHandler<TException extends Throwable> extends AbstractExceptionsHandler
{
  
  protected AbstractSpecificExceptionHandler(ExceptionHandler wrapped, Class<TException> exceptionType)
  {
    super(wrapped, wrap(exceptionType));
  }
  
  @Override
  @SuppressWarnings("unchecked")
  protected boolean doHandle(Throwable exception)
  {
    TException typedException = (TException)exception;
    return this.handleCustom(typedException);
  }
  
  protected abstract boolean handleCustom(TException exception);
  
  private static Collection<Class<? extends Throwable>> wrap(Class<? extends Throwable> exceptionType)
  {
    Collection<Class<? extends Throwable>> wrapped = new ArrayList<Class<? extends Throwable>>(1);
    wrapped.add(exceptionType);
    return wrapped;
  }
}
