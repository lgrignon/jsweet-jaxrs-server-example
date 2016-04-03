package org.jsweet.ionicexercise.entities;

import java.util.Objects;

/**
 * Base class for ZgEntities
 * 
 * @author lgrignon
 * @param <TId>
 *            Object's identity java type
 * 
 */
public abstract class AbstractEntity<TId> {

	public abstract TId getId();

	@Override
	public int hashCode() {
		TId id = getId();
		return id == null ? 0 : id.hashCode();
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractEntity)) {
			return false;
		}

		AbstractEntity<TId> other = (AbstractEntity<TId>) obj;
		if (!getEntityClass().equals(other.getEntityClass())) {
			return false;
		}

		return Objects.equals(getId(), other.getId());
	}

	protected abstract Class<? extends AbstractEntity<TId>> getEntityClass();
}
