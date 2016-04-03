package org.jsweet.ionicexercise.configuration.errors.builder;

import javax.faces.context.ExceptionHandler;

/**
 * Null object - Returns child handler when asked for handler
 * 
 * @author lgrignon
 * 
 */
class NullHandlerBuilder extends AbstractHandlerBuilder
{
  
  NullHandlerBuilder()
  {
    super(null);
  }
  
  @Override
  public ExceptionHandler build(ExceptionHandler childHandler)
  {
    return childHandler;
  }
}
