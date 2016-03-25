package fr.jerodia.webapi;

import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

import fr.jerodia.configuration.ApplicationBeans;

@Provider
public class InjectionProvider implements InjectableProvider<Inject, Type> {

	public ComponentScope getScope() {
		return ComponentScope.Singleton;
	}

	@Override
	public Injectable<?> getInjectable(ComponentContext context,
			Inject injectAnno, Type t) {
		if (!(t instanceof Class))
			throw new RuntimeException("not injecting a class type ?");

		Class<?> clazz = (Class<?>) t;

		final Object instance = ApplicationBeans.get(clazz);

		return new Injectable<Object>() {
			public Object getValue() {
				return instance;
			}
		};
	}
}