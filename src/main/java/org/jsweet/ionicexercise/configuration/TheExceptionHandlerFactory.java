package org.jsweet.ionicexercise.configuration;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.jsweet.ionicexercise.Logger;
import org.jsweet.ionicexercise.configuration.errors.ICustomExceptionHandler;
import org.jsweet.ionicexercise.configuration.errors.builder.AbstractHandlerBuilder;
import org.jsweet.ionicexercise.configuration.errors.builder.HandleExceptions;

/**
 * Builds ExceptionNameAsOutcomeExceptionHandler
 * 
 * @author lgrignon
 * 
 */
public class TheExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory
{
  
  protected static final Logger log = Logger.getLogger(TheExceptionHandlerFactory.class);
  
  private ExceptionHandlerFactory parent;
  
  private AbstractHandlerBuilder builder;
  
  /**
   * Builds factory
   * 
   * @param parent
   *          Parent factory
   */
  public TheExceptionHandlerFactory(ExceptionHandlerFactory parent)
  {
    this.parent = parent;
  }
  
  @Override
  public ExceptionHandler getExceptionHandler()
  {
    if (this.builder == null)
    {
      this.createExceptionHandlerBuilder();
    }
    
    return this.builder.build(parent.getExceptionHandler());
  }
  
  // All varargs are throwable...
  @SuppressWarnings("unchecked")
  private void createExceptionHandlerBuilder()
  {
    this.builder = HandleExceptions
        .ofTypes(Exception.class)
        .withCustomHandler(TestErrorHandler.class);
  }
  
  private static class TestErrorHandler<TException extends Throwable> implements ICustomExceptionHandler<TException> {
	  
	  private final static Logger log = Logger.getLogger(TestErrorHandler.class);
	  
	  @Override
	public boolean handle(TException exception) {
		  log.error("FATAL ERROR OCCURRED - implement handler", exception);
		return true;
	}
  }
}
