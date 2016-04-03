package org.jsweet.ionicexercise.configuration;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manages configurations<br />
 * 
 * Property search order: <br/>
 * 1) External configuration <br/>
 * 2) Application configuration <br/>
 * 
 * - Please use <b>application.properties</b> as your application's properties
 * filename (make it available in classpath) <br />
 * - Please use labels.properties as your application's labels filename
 * (available in classpath as well) <br />
 * 
 * - Defines <b>application.name</b> property <br />
 * 
 * - If you want to use the external (environment specific) configuration file
 * functionality. You must define an <b><ApplicationName>.conf.ext.path</b>
 * environment variable pointing to your external configuration file.<br />
 * 
 * - If you want to use dynamic messages module, please provide following
 * properties:<br />
 * <ul>
 * <li><b>internationalization.labels.dir</b>: Path to the label bundles
 * directory</li>
 * <li><b>internationalization.labels.bundlename.format</b>: Bundles' filename
 * format (Format param is locale name)</li>
 * <li><b>internationalization.labels.defaultlocale</b>: (Optional) Default is
 * Locale.US</li>
 * </ul>
 * <br />
 * 
 * @author lgrignon
 * @version 1.1.0
 */
@ApplicationScoped
public class Configuration implements Serializable {

	private final static long serialVersionUID = 0L;

	private final static String APP_PROP_FILENAME = "application.properties";
	private final static String APP_LABELS_FILENAME = "labels.properties";

	private final static String EXT_PROP_FILENAME_FORMAT_ENVVAR = "%s.conf.ext.path";

	private final static String APP_NAME_KEY = "app.name";
	private final static String DYNAMIC_MESSAGES_BUNDLES_DIR_KEY = "internationalization.labels.dir";
	private final static String DYNAMIC_MESSAGES_BUNDLENAME_FORMAT_KEY = "internationalization.labels.bundlename.format";
	private final static String DYNAMIC_MESSAGES_DEFAULT_LOCALE_KEY = "internationalization.labels.defaultlocale";

	private final static Logger logger = Logger.getLogger(Configuration.class);

	private Properties ext;
	private DynamicMessages dynamicMessages;
	private Properties applicationProps;
	private Properties applicationLabels;

	/**
	 * Returns parameter value
	 * 
	 * @param key
	 *            Parameter key
	 * @param args
	 *            Format parameters
	 * @return Parameter value
	 */

	public String label(final String key, final Object... args) {
		String label = searchProperty(key, getApplicationLabels());
		return label == null ? null : String.format(label, args);
	}

	public String property(final String key) {
		return searchProperty(key, getExternal(), getApplication());
	}

	public Float propertyAsFloat(final String key) {
		return Float.parseFloat(property(key));
	}

	public Integer propertyAsInt(final String key) {
		return Integer.parseInt(property(key));
	}

	/**
	 * Retourne le gestionnaire des messages dynamiques pour cette
	 * configuration.
	 * 
	 * @return Le gestionnaire des messages dynamiques pour cette configuration.
	 */
	public DynamicMessages getDynamicMessages() {
		if (this.dynamicMessages == null) {
			loadDynamicMessages();
		}

		return this.dynamicMessages;
	}

	private Properties getExternal() {
		if (this.ext != null) {
			return this.ext;
		}

		this.ext = new Properties();

		String appName = searchProperty(APP_NAME_KEY, getApplication());
		if (appName == null) {
			logger.warn(String.format("No application name defined : key=%s", APP_NAME_KEY));
			return this.ext;
		}

		final String extPath = System.getProperty(String.format(EXT_PROP_FILENAME_FORMAT_ENVVAR, appName));
		if (extPath == null) {
			logger.info("No external conf file found in configuration");
		} else {
			final Properties loaded = PropertiesHelper.loadPropertiesFromPath(extPath);
			if (loaded == null) {
				logger.warn("Failed to read external configuration");
			} else {
				this.ext = loaded;
			}
		}

		return this.ext;
	}

	private Properties getApplication() {
		if (this.applicationProps == null) {
			this.applicationProps = loadOptionalProperties(APP_PROP_FILENAME);
		}

		return this.applicationProps;
	}

	private Properties getApplicationLabels() {
		if (this.applicationLabels == null) {
			this.applicationLabels = loadOptionalProperties(APP_LABELS_FILENAME);
		}

		return this.applicationLabels;
	}

	private void loadDynamicMessages() {
		final String bundleDirectoryPath = property(DYNAMIC_MESSAGES_BUNDLES_DIR_KEY);
		if (StringUtils.isBlank(bundleDirectoryPath)) {
			throw new ConfigurationException("Missing internationalization configuration: %s",
					DYNAMIC_MESSAGES_BUNDLES_DIR_KEY);
		}

		String bundleNameFormat = property(DYNAMIC_MESSAGES_BUNDLENAME_FORMAT_KEY);
		if (StringUtils.isBlank(bundleNameFormat)) {
			bundleNameFormat = "messages_%s.properties";
			logger.debug(String.format("No format defined for messages' filename (%s), defaulting to: %s",
					DYNAMIC_MESSAGES_BUNDLENAME_FORMAT_KEY, bundleNameFormat));
		}

		final String defaultLocaleName = property(DYNAMIC_MESSAGES_DEFAULT_LOCALE_KEY);
		final File bundlesDirectory = new File(bundleDirectoryPath);

		// Recupere la locale par defaut
		Locale defaultLocale = null;
		if (defaultLocaleName != null) {
			defaultLocale = LocaleUtils.toLocale(defaultLocaleName);
		}

		try {
			this.dynamicMessages = new DynamicMessages(bundlesDirectory, bundleNameFormat, defaultLocale);
		} catch (IOException e) {
			throw new ConfigurationException("Can't create dynamic messages", e);
		}
	}

	private Properties loadOptionalProperties(String filename) {
		if (StringUtils.isNotBlank(filename)) {
			Properties read = PropertiesHelper.loadPropertiesFromClassLoader(filename);
			logger.info(String.format("Optional properties found: %s", filename));
			if (read != null) {
				return read;
			}

			logger.warn(String.format("Can't read optional properties whereas it was specified: %s", filename));
		}

		return new Properties();
	}

	private String searchProperty(String propertyName, Properties... properties) {
		logger.trace(String.format("Searching [%s] in configuration", propertyName));

		String propertyValue = null;
		for (final Properties propertySet : properties) {
			logger.trace(String.format("Trying in %s", propertySet));
			propertyValue = propertySet.getProperty(propertyName);
			if (propertyValue != null) {
				logger.trace(String.format("Found: %s", propertyValue));
				break;
			}
		}

		return propertyValue;
	}
}
