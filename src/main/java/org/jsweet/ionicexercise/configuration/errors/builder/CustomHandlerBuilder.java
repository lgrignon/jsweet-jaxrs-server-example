package org.jsweet.ionicexercise.configuration.errors.builder;

import javax.faces.context.ExceptionHandler;

import org.jsweet.ionicexercise.configuration.errors.ICustomExceptionHandler;
import org.jsweet.ionicexercise.configuration.errors.UserDefinedExceptionHandler;

/**
 * Builds redirector handlers
 * 
 * @author lgrignon
 * @param <TException>
 *          Handled exception type
 * 
 * @see RedirectorExceptionHandler
 */
public class CustomHandlerBuilder<TException extends Throwable> extends AbstractHandlerBuilder
{
  
  private final Class<TException> exceptionType;
  private final ICustomExceptionHandler<TException> handler;
  
  /**
   * Creates builder
   * 
   * @param parentHandlerBuilder
   *          Parent handler builder
   * 
   * @param exceptionType
   *          Handled exception type
   * 
   */
  protected CustomHandlerBuilder(
      AbstractHandlerBuilder parentHandlerBuilder,
      Class<TException> exceptionType,
      ICustomExceptionHandler<TException> handler)
  {
    super(parentHandlerBuilder);
    this.handler = handler;
    this.exceptionType = exceptionType;
  }
  
  @Override
  public ExceptionHandler build(final ExceptionHandler childHandler)
  {
    UserDefinedExceptionHandler<TException> handler = new UserDefinedExceptionHandler<TException>(
        childHandler,
        this.exceptionType,
        this.handler);
    
    return parentBuilder.build(handler);
  }
}
