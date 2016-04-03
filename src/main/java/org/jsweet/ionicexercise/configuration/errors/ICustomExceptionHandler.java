package org.jsweet.ionicexercise.configuration.errors;

/**
 * Interface for defining custom exception handlers for your application
 * 
 * @author lgrignon
 * @param <TException>
 * 
 */
public interface ICustomExceptionHandler<TException>
{
  
  /**
   * Handles exception
   * 
   * @param exception
   *          Thrown exception
   * @return false if exception should not be handled by this handler!
   */
  boolean handle(final TException exception);
}
