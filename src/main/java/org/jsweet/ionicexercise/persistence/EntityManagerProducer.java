package org.jsweet.ionicexercise.persistence;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.jsweet.ionicexercise.configuration.Configuration;

/**
 * Produces persistence contexts
 * 
 * @author lgrignon
 * 
 */
@ApplicationScoped
public class EntityManagerProducer {

	protected static Logger logger = Logger.getLogger(EntityManagerProducer.class);

	@Inject
	protected Configuration configuration;

	private EntityManagerFactory entityManagerFactory;

	/**
	 * Initializes producer
	 */
	@PostConstruct
	public void create() {
		try {
			final String persistenceUnit = configuration.property("persistence.unit.name");
			this.entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnit);
		} catch (Exception e) {
			logger.error("error during persistence unit initialization", e);
			throw e;
		}
	}

	/**
	 * Produces entity managers
	 * 
	 * @return New entity manager
	 */
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		logger.trace("Producing new entity manager");
		return this.entityManagerFactory.createEntityManager();
	}

	/**
	 * Releases entity manager
	 * 
	 * @param entityManager
	 *            Entity manager to be released
	 */
	public void closeEntityManager(@Disposes EntityManager entityManager) {
		logger.trace("Disposing entity manager");
		if (entityManager != null && entityManager.isOpen()) {
			logger.trace("Closing entity manager");
			entityManager.close();
		}
	}
}
