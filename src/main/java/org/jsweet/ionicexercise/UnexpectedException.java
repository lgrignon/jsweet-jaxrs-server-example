package org.jsweet.ionicexercise;

/**
 * Cette exception est jetee lorsqu'une erreur inattendue survient.
 * 
 * @author lgrignon
 * @version 1.0.0
 */
public class UnexpectedException extends RuntimeException
{
  
  private final static long serialVersionUID = 0L;
  
  /**
   * Construit une nouvelle <tt>UnexpectedException</tt> sans informations.
   */
  public UnexpectedException()
  {
    super();
  }
  
  /**
   * Construit une nouvelle <tt>UnexpectedException</tt> en precisant un message d'erreur.
   * 
   * @param message
   *          Le message d'erreur associe e cette exception.
   */
  public UnexpectedException(final String message)
  {
    super(message);
  }
  
  /**
   * Construit une nouvelle <tt>UnexpectedException</tt> en la cause de l'exception.
   * 
   * @param cause
   *          La cause de l'exception.
   */
  public UnexpectedException(final Throwable cause)
  {
    super(cause);
  }
  
  /**
   * Construit une nouvelle <tt>UnexpectedException</tt> en precisant un message d'erreur et la cause de l'exception.
   * 
   * @param message
   *          Le message d'erreur associe e cette exception.
   * @param formatParams
   *          Message format parameters
   */
  public UnexpectedException(final String message, final Object... formatParams)
  {
    super(String.format(message, formatParams));
  }
  
  /**
   * Construit une nouvelle <tt>UnexpectedException</tt> en precisant un message d'erreur et la cause de l'exception.
   * 
   * @param message
   *          Le message d'erreur associe e cette exception.
   * @param cause
   *          Exception cause
   * @param formatParams
   *          Message format parameters
   */
  public UnexpectedException(final String message, final Throwable cause, final Object... formatParams)
  {
    super(String.format(message, formatParams), cause);
  }
  
}
