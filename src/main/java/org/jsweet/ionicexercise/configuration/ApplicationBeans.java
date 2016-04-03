package org.jsweet.ionicexercise.configuration;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jsweet.ionicexercise.UnexpectedException;

/**
 * Gives direct access to managed beans - Designed to be used from unmanaged
 * code
 * 
 * @author Louis Grignon
 * 
 */
@ApplicationScoped
public class ApplicationBeans {

	protected static ApplicationBeans instance;

	@Inject
	private BeanManager beanManager;

	/**
	 * Gets instance
	 * 
	 * @return Instance from managed environment
	 */
	public static ApplicationBeans instance() {
		if (instance == null) {
			BeanManager beanManager;
			InitialContext ctx = null;
			try {
				ctx = new InitialContext();
				beanManager = (BeanManager) ctx.lookup("java:comp/BeanManager");
			} catch (NamingException e) {
				try {
					beanManager = (BeanManager) ctx.lookup("java:app/BeanManager");
				} catch (NamingException ne) {
					throw new UnexpectedException("Unable to obtain BeanManager.", ne);
				}
			}

			instance = getBeanFromManager(beanManager, ApplicationBeans.class);
		}

		return instance;
	}

	/**
	 * Gets bean instance from context
	 * 
	 * @param <T>
	 *            Bean's type
	 * @param beanType
	 *            Bean's type
	 * @param annotations
	 *            Bean's annotations
	 * @return Bean instance or null if no
	 */
	public static <T> T get(final Class<T> beanType, Annotation... annotations) {
		return instance().getBean(beanType, annotations);
	}

	/**
	 * Gets bean instance from context
	 * 
	 * @param <T>
	 *            Bean's type
	 * @param beanType
	 *            Bean's type
	 * @param annotations
	 *            Bean's annotations
	 * @return Bean instance or null if no
	 */
	public <T> T getBean(final Class<T> beanType, Annotation... annotations) {
		return getBeanFromManager(beanManager, beanType, annotations);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getBeanFromManager(BeanManager beanManager, final Class<T> beanType,
			Annotation... annotations) {
		Set<Bean<?>> beans = beanManager.getBeans(beanType, annotations);
		if (beans.size() > 1) {
			throw new UnexpectedException("Many bean declarations found for type %s (%s)", beanType.getSimpleName(),
					beansToString(beans));
		}

		if (beans.isEmpty()) {
			throw new UnexpectedException("No bean declaration found for type %s", beanType.getSimpleName());
		}

		final Bean<T> bean = (Bean<T>) beans.iterator().next();
		final CreationalContext<T> context = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, beanType, context);
	}

	private static String beansToString(Collection<Bean<?>> beans) {
		String[] beansLabels = new String[beans.size()];
		int i = 0;
		for (final Bean<?> bean : beans) {
			beansLabels[i++] = bean.getName();
		}

		return Arrays.toString(beansLabels);
	}

}
