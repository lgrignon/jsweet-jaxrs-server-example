package org.jsweet.ionicexercise.configuration.errors;

import java.util.Collection;

import javax.faces.context.ExceptionHandler;

/**
 * Handles all exceptions of given type the same way - redirecting to given view id
 * 
 * @author lgrignon
 */
public class ThrowAnotherExceptionExceptionHandler extends AbstractExceptionsHandler
{
  
  private RuntimeException exception;
  
  /**
   * Wraps secondary exception handler and specify outcome returned when exception occurred
   * 
   * @param wrapped
   *          Secondary exception handler
   * @param exception
   *          Other exception to be thrown
   * @param typesOfException
   *          Handled Exception types
   */
  public ThrowAnotherExceptionExceptionHandler(final ExceptionHandler wrapped, final Collection<Class<? extends Throwable>> typesOfException,
      final RuntimeException exception)
  {
    super(wrapped, typesOfException);
    this.exception = exception;
  }
  
  @Override
  protected HandlingStatus handle(Throwable exception)
  {
    throw this.exception;
  }
  
  @Override
  protected boolean doHandle(Throwable exception)
  {
    throw new UnsupportedOperationException("SHOULD NOT BE CALLED!");
  }
}
