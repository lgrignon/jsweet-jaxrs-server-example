package org.jsweet.ionicexercise.configuration.errors;

import javax.faces.context.ExceptionHandler;

/**
 * Exception handler which delegates its calls
 * 
 * @author lgrignon
 * 
 * @param <TException>
 */

public class UserDefinedExceptionHandler<TException extends Throwable> extends AbstractSpecificExceptionHandler<TException>
{
  
  private ICustomExceptionHandler<TException> delegateHandler;
  
  /**
   * Builds handler
   * 
   * @param wrapped
   *          Parent exception handler
   * @param exceptionType
   *          Handled exception type
   * @param userDefinedHandler
   *          Custom exception handler
   */
  public UserDefinedExceptionHandler(
      ExceptionHandler wrapped,
      Class<TException> exceptionType,
      ICustomExceptionHandler<TException> userDefinedHandler)
  {
    super(wrapped, exceptionType);
    this.delegateHandler = userDefinedHandler;
  }
  
  @Override
  protected boolean handleCustom(TException exception)
  {
    return delegateHandler.handle(exception);
  }
}
