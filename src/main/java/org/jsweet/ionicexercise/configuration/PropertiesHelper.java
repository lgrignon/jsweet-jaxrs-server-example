package org.jsweet.ionicexercise.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utilitaires de manipulation des Properties.
 * 
 * @author lgrignon
 * 
 */
public abstract class PropertiesHelper {

	private final static Logger LOG = Logger.getLogger(PropertiesHelper.class);

	/**
	 * Retourne les cles du properties donne.
	 * 
	 * @param properties
	 *            Les properties depuis lesquelles extraire.
	 * @return Les cles du properties donne.
	 */
	public static Set<String> keys(final Properties properties) {
		return keys(properties, true);
	}

	/**
	 * Retourne les cles du properties donne.
	 * 
	 * @param properties
	 *            Les properties depuis lesquelles extraire.
	 * @param stringsOnly
	 *            Si l'on ne conserve que les entrees avec cle ET valeur de type
	 *            string (ex : une entree avec cle ou valeur Integer ne sera pas
	 *            ajoutee)
	 * @return Les cles du properties donne.
	 */
	public static Set<String> keys(final Properties properties, final boolean stringsOnly) {
		final Set<Object> oKeys = properties.keySet();
		final Set<String> keys = new HashSet<String>(oKeys.size());
		for (final Object oKey : oKeys) {
			if (!stringsOnly || oKey instanceof String) {
				keys.add(oKey == null ? "" : oKey.toString());
			}
		}
		return keys;
	}

	/**
	 * Retourne une map de String correspondant aux proprietes donnees. (Utilise
	 * toString sur tous les elements de la liste)
	 * 
	 * @param properties
	 *            Les properties e convertir.
	 * @return La map.
	 */
	public static Map<String, String> asMap(final Properties properties) {
		return asMap(properties, true);
	}

	/**
	 * Retourne une map de String correspondant aux proprietes donnees. (Utilise
	 * toString sur tous les elements de la liste)
	 * 
	 * @param properties
	 *            Les properties e convertir.
	 * @param stringsOnly
	 *            Si l'on ne conserve que les entrees avec cle ET valeur de type
	 *            string (ex : une entree avec cle ou valeur Integer ne sera pas
	 *            ajoutee)
	 * @return La map.
	 */
	public static Map<String, String> asMap(final Properties properties, final boolean stringsOnly) {
		final Map<String, String> propsMap = new HashMap<String, String>(properties.size());
		for (final Map.Entry<Object, Object> prop : properties.entrySet()) {
			if (!stringsOnly || prop.getKey() instanceof String && prop.getValue() instanceof String) {
				final String sKey = prop.getKey() == null ? "" : prop.getKey().toString();
				final String sVal = prop.getValue() == null ? "" : prop.getValue().toString();
				propsMap.put(sKey, sVal);
			}
		}
		return propsMap;
	}

	/**
	 * Retourne un Properties en cherchant dans le classpath un fichier de
	 * properties de nom donne ou null si la lecture echoue.
	 * 
	 * @param filename
	 *            Le nom e chercher.
	 * @return Un Properties en cherchant dans le classpath un fichier de
	 *         properties de nom donne ou null si la lecture echoue.
	 */
	public static Properties loadPropertiesFromClassLoader(final String filename) {
		final ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
		Properties loaded = null;
		final InputStream propIS = ctxClassLoader.getResourceAsStream(filename);
		try {
			if (propIS == null) {
				LOG.warn(String.format("%s not found in class path", filename));
			} else {
				try {
					final Properties tempProps = new Properties();
					tempProps.load(propIS);
					loaded = tempProps;
				} finally {
					propIS.close();
				}
			}
		} catch (IOException e) {
			LOG.error(String.format("Failed to read properties from %s", e, ctxClassLoader.getResource(filename)));
		}
		return loaded;
	}

	/**
	 * Retourne un Properties e partir d'un fichier de properties de chemin
	 * donne ou null si la lecture echoue ou si le fichier n'existe pas.
	 * 
	 * @param path
	 *            Le chemin du fichier e chercher.
	 * @return Un Properties e partir d'un fichier de properties de chemin donne
	 *         ou null si la lecture echoue ou si le fichier n'existe pas.
	 */
	public static Properties loadPropertiesFromPath(final String path) {
		return loadPropertiesFromFile(new File(path));
	}

	/**
	 * Retourne un Properties e partir d'un fichier de properties donne ou null
	 * si la lecture echoue ou si le fichier n'existe pas.
	 * 
	 * @param file
	 *            Le fichier e recuperer.
	 * @return Un Properties e partir d'un fichier de properties donne ou null
	 *         si la lecture echoue ou si le fichier n'existe pas.
	 */
	public static Properties loadPropertiesFromFile(final File file) {
		Properties loaded = null;
		try {
			final Properties tempProps = new Properties();
			final FileInputStream propsInput = new FileInputStream(file);
			try {
				tempProps.load(propsInput);
				loaded = tempProps;
			} finally {
				propsInput.close();
			}
		} catch (FileNotFoundException e) {
			LOG.warn(String.format("Properties file not found : %s", e, file));
		} catch (IOException e) {
			LOG.warn(String.format("Failed to read properties file : %s", e, file));
		}

		return loaded;
	}

	/**
	 * Ecrit le properties passe vers le fichier de sortie donne.
	 * 
	 * @param properties
	 *            Les properties e persister.
	 * @param output
	 *            Le fichier de sortie.
	 * @throws IOException
	 *             Si une erreur survient.
	 */
	public static void writePropertiesToFile(final Properties properties, final File output) throws IOException {
		final FileOutputStream fileEnOut = new FileOutputStream(output);
		try {
			properties.store(fileEnOut, StringUtils.EMPTY);
		} finally {
			fileEnOut.close();
		}
	}
}
