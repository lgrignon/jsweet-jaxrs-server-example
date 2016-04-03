package org.jsweet.ionicexercise.configuration.errors.builder;

import java.util.Collection;

import javax.faces.context.ExceptionHandler;

import org.jsweet.ionicexercise.configuration.errors.ThrowAnotherExceptionExceptionHandler;

/**
 * Builds redirector handlers
 * 
 * @author lgrignon
 * 
 * @see RedirectorExceptionHandler
 */
public class ThrowAnotherExceptionHandlerBuilder extends AbstractHandlerBuilder
{
  
  private final Collection<Class<? extends Throwable>> exceptionTypes;
  private RuntimeException exception;
  
  /**
   * Creates builder
   * 
   * @param parentHandlerBuilder
   *          Parent handler builder
   * 
   * @param exceptionTypes
   *          Handled exception type
   * 
   */
  protected ThrowAnotherExceptionHandlerBuilder(AbstractHandlerBuilder parentHandlerBuilder, Collection<Class<? extends Throwable>> exceptionTypes)
  {
    super(parentHandlerBuilder);
    this.exceptionTypes = exceptionTypes;
  }
  
  /**
   * Errors will trigger this exception to be thrown
   * 
   * @param exception
   *          Exception to be thrown on error
   * @return Self reference
   */
  public ThrowAnotherExceptionHandlerBuilder produceException(final RuntimeException exception)
  {
    this.exception = exception;
    return this;
  }
  
  @Override
  public ExceptionHandler build(final ExceptionHandler childHandler)
  {
    return parentBuilder.build(new ThrowAnotherExceptionExceptionHandler(childHandler, exceptionTypes, exception));
  }
}
