package org.jsweet.ionicexercise.configuration.errors.builder;

import java.util.Arrays;

import javax.faces.context.ExceptionHandler;

import org.jsweet.ionicexercise.configuration.errors.builder.HandleExceptions.ExceptionHandlersBuilder;

/**
 * Base class for handler builders. Default parent is NullHandlerBuilder. Default build operation delegates to parent
 * builder
 * 
 * @author lgrignon
 */
public abstract class AbstractHandlerBuilder
{
  
  protected AbstractHandlerBuilder parentBuilder;
  
  protected AbstractHandlerBuilder()
  {
    this(new NullHandlerBuilder());
  }
  
  protected AbstractHandlerBuilder(AbstractHandlerBuilder parentHandlerBuilder)
  {
    this.parentBuilder = parentHandlerBuilder;
  }
  
  /**
   * Builds handler
   * 
   * @param childHandler
   *          Child handler
   * @return Appropriate handler
   */
  public ExceptionHandler build(ExceptionHandler childHandler)
  {
    return this.parentBuilder.build(childHandler);
  }
  
  /**
   * Specifies other exception's handling method
   * 
   * @param handledExceptionTypes
   *          Handled exception types
   * @return Builder
   */
  @SafeVarargs
  public final ExceptionHandlersBuilder andThoseOfTypes(Class<? extends Throwable>... handledExceptionTypes)
  {
    return new ExceptionHandlersBuilder(this, Arrays.asList(handledExceptionTypes));
  }
}
