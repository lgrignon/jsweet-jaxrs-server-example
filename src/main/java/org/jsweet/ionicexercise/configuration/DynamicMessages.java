package org.jsweet.ionicexercise.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Gere un ensemble dynamique de messages en fonction des locales.
 * 
 * @author lgrignon
 * @see Configuration
 */
public class DynamicMessages implements Serializable {

	private final static long serialVersionUID = 0L;
	private final static Logger LOG = Logger.getLogger(DynamicMessages.class);

	private final transient Object lock = new Object();
	private File bundlesDirectory;
	private String bundleNameFormat;
	private Pattern bundleFilenamePattern;
	private Map<Locale, Properties> bundles;
	private Locale defaultLocale;

	/**
	 * Construit le gestionnaire de messages internationalises.
	 * 
	 * @param bundlesDirectory
	 *            Le repertoire contenant les fichiers de bundle.
	 * @param bundleNameFormat
	 *            Le format des noms de fichier des bundles dans le repertoire,
	 *            avec %s remplaeant la position de la locale. Par exemple :
	 *            bundle_%s.properties
	 * @param defaultLocale
	 *            La locale utilisee lorsque la locale specifiee ne peut etre
	 *            utilisee. Si null, en_US est utilisee.
	 * @throws IOException
	 *             Si le chemin passe n'est pas un dossier ou est illisible.
	 */
	public DynamicMessages(final File bundlesDirectory, final String bundleNameFormat, final Locale defaultLocale)
			throws IOException {
		// Teste le repertoire des bundles
		this.bundlesDirectory = bundlesDirectory;
		this.bundlesDirectory.mkdirs();
		if (!this.bundlesDirectory.isDirectory() || !this.bundlesDirectory.canRead()) {
			throw new IOException(String.format("Dynamic message bundles directory isn't readable (%s)",
					bundlesDirectory.getAbsolutePath()));
		}

		this.bundleNameFormat = bundleNameFormat;

		// Determine la locale par defaut
		this.defaultLocale = defaultLocale;
		if (this.defaultLocale == null) {
			this.defaultLocale = Locale.US;
		}

		final String escapedFormat = escapeRegex(this.bundleNameFormat);
		// en, en_US ou en_US_Ef
		final String regex = String.format(escapedFormat, "([a-z]{2}_?)([A-Z]{2}_?)?([a-zA-Z]{2})?");
		this.bundleFilenamePattern = Pattern.compile(regex);

		LOG.debug(String.format("Built : %s", finalToDebug()));
	}

	private final static Pattern GRAB_SP_CHARS = Pattern.compile("([\\\\*+\\[\\](){}\\$.?\\^|])");

	/**
	 * This function will escape special characters within a string to ensure
	 * that the string will not be parsed as a regular expression. This is
	 * helpful with accepting using input that needs to be used in functions
	 * that take a regular expression as an argument (such as
	 * String.replaceAll(), or String.split()).
	 * 
	 * @param regex
	 *            - argument which we wish to escape.
	 * @return - Resulting string with the following characters escaped:
	 *         [](){}+*^?$.\
	 */
	public static String escapeRegex(final String regex) {
		final Matcher match = GRAB_SP_CHARS.matcher(regex);
		return match.replaceAll("\\\\$1");
	}

	protected DynamicMessages() {
	}

	public String toDebugString() {
		return finalToDebug();
	}

	/**
	 * Retourne les messages geres par ce composant pour la locale par defaut.
	 * 
	 * @return Les messages geres par ce composant pour la locale par defaut.
	 */
	public Properties getDefaultMessages() {
		return getMessages(this.defaultLocale);
	}

	/**
	 * Retourne les messages geres par ce composant pour la locale donnee.
	 * 
	 * @param locale
	 *            La locale pour laquelle recuperer les messages.
	 * @return Les messages geres par ce composant pour la locale donnee.
	 */
	public Properties getMessages(final Locale locale) {
		return getMessages(locale, true);
	}

	/**
	 * Retourne le message de cle donnee pour la locale donne.
	 * 
	 * @param key
	 *            La cle du message.
	 * @param locale
	 *            La locale pour laquelle recuperer le message.
	 * @return Le message de cle donnee pour la locale donne.
	 */
	public String getMessage(final String key, final Locale locale) {
		final Properties localizedMessage = getMessages(locale);
		String message = null;
		if (localizedMessage != null) {
			message = localizedMessage.getProperty(key);
		}

		return message;
	}

	/**
	 * Efface tous les messages chargesen cache.
	 */
	public void clearLoadedMessages() {
		synchronized (lock) {
			this.bundles = null;
		}
	}

	/**
	 * Retourne toutes les cles contenues dans les bundles connus.
	 * 
	 * @return Toutes les cles contenues dans les bundles connus.
	 */
	public Collection<String> getAllKeys() {
		final Map<Locale, Properties> bundles = getAllBundles();
		final Set<String> allKeys = new HashSet<String>();
		for (final Properties bundle : bundles.values()) {
			allKeys.addAll(PropertiesHelper.keys(bundle));
		}
		return allKeys;
	}

	/**
	 * Retourne toutes les locales disponibles.
	 * 
	 * @return Toutes les locales disponibles.
	 */
	public Collection<Locale> getAllLocales() {
		return getAllBundles().keySet();
	}

	/**
	 * Retourne les locales disponibles pour le label donne.
	 * 
	 * @param label
	 *            Le label pour lequel rechercher.
	 * @return Les locales disponibles pour le label donne.
	 */
	public Collection<Locale> getAvailablesLocalesForLabel(final String label) {
		return getLabelTranslations(label).keySet();
	}

	/**
	 * Retourne toutes les traductions du label donne.
	 * 
	 * @param label
	 *            Le label pour lequel rechercher.
	 * @return Les traductions du label donne.
	 */
	public Map<Locale, String> getLabelTranslations(final String label) {
		final Map<Locale, Properties> bundles = getAllBundles();
		final Map<Locale, String> translations = new HashMap<Locale, String>(bundles.size());
		for (final Map.Entry<Locale, Properties> bundle : bundles.entrySet()) {
			if (bundle.getValue().containsKey(label)) {
				translations.put(bundle.getKey(), bundle.getValue().getProperty(label));
			}
		}
		return translations;
	}

	/**
	 * Si une erreur survient e la sauvegarde d'une des locales. Si une erreur
	 * survient dans la sauvegarde d'un des fichiers, la sauvegarde des autres
	 * sera tout de meme tentee. C'est la premiere exception jetee qui est
	 * propagee.
	 * 
	 * @param label
	 *            Le label dont on va sauvegarder les traductions.
	 * @param translations
	 *            Les traductions de ce label.
	 * @throws IOException
	 *             Si une erreur survient dans la sauvegarde d'un des fichiers,
	 *             la sauvegarde des autres sera tout de meme tentee. C'est la
	 *             premiere exception jetee qui est propagee.
	 */
	public void saveLabelTranslations(final String label, final Map<Locale, String> translations) throws IOException {
		synchronized (lock) {
			final Map<Locale, Properties> messages = getAllBundles();
			for (final Map.Entry<Locale, String> translation : translations.entrySet()) {
				final Locale locale = translation.getKey();
				Properties bundle = messages.get(locale);
				LOG.debug(String.format("Bundle stored for %s : %s", locale, bundle));
				if (bundle == null) {
					bundle = new Properties();
					messages.put(locale, bundle);
				}
				bundle.setProperty(label, translation.getValue());
				LOG.debug(String.format("Bundle updated for %s : %s", locale, bundle));
			}
			saveAllMessages();
		}
	}

	/**
	 * Deletes given label for all existing locales
	 * 
	 * @param label
	 *            Label to be deleted
	 * @throws IOException
	 *             If an error occured saving messages
	 */
	public void deleteLabel(final String label) throws IOException {
		synchronized (lock) {
			final Map<Locale, Properties> messages = getAllBundles();
			for (final Properties localMessages : messages.values()) {
				if (localMessages.containsKey(label)) {
					localMessages.remove(label);
				}
			}
			saveAllMessages();
		}
	}

	/**
	 * Sauvegarde les messages pour la locale donnee.
	 * 
	 * @param locale
	 *            La locale pour laquelle sauvegarder les messages.
	 * @throws IOException
	 *             Si il est impossible d'ecrire.
	 */
	public void saveMessages(final Locale locale) throws IOException {
		final String filename = String.format(this.bundleNameFormat, locale);
		final File output = new File(this.bundlesDirectory, filename);
		LOG.info(String.format("Saving messages %s to %s", locale, output.getAbsolutePath()));
		final Properties messages = getAllBundles().get(locale);
		synchronized (lock) {
			try (FileOutputStream outStream = new FileOutputStream(output)) {
				messages.store(outStream, "Generated by ZGSoft");
			}
		}
	}

	/**
	 * Sauvegarde les messages de toutes les locales. Si une erreur survient
	 * dans la sauvegarde d'un des fichiers, la sauvegarde des autres sera tout
	 * de meme tentee.
	 * 
	 * @throws IOException
	 *             Si une erreur survient dans la sauvegarde d'un des fichiers,
	 *             la sauvegarde des autres sera tout de meme tentee. C'est la
	 *             premiere exception jetee qui est propagee.
	 */
	public void saveAllMessages() throws IOException {
		saveAllMessages(getAllBundles().keySet());
	}

	/**
	 * Retourne la locale utilisee lorsque la locale specifiee ne peut etre
	 * utilisee.
	 * 
	 * @return La locale utilisee lorsque la locale specifiee ne peut etre
	 *         utilisee.
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Positionne la locale utilisee lorsque la locale specifiee ne peut etre
	 * utilisee.
	 * 
	 * @param defaultLocale
	 *            La locale utilisee lorsque la locale specifiee ne peut etre
	 *            utilisee.
	 */
	public void setDefaultLocale(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Compresses all labels for all locales and gets a stream on compressed
	 * file
	 * 
	 * @return Read stream to compressed labels (ZIP file)
	 * @throws IOException
	 *             If an error occurred compressing or reading files.
	 */
	public InputStream compressLabels() throws IOException {
		final File compressedFile = createCompressedLabelsFile();
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(compressedFile))) {
			for (final File bundleFile : getBundleFiles()) {
				writeBundleToZip(zipOut, bundleFile);
			}
		}

		return new FileInputStream(compressedFile);
	}

	protected Map<Locale, Properties> getAllBundles() {
		synchronized (lock) {
			if (this.bundles == null) {
				Collection<File> bundleFiles = getBundleFiles();
				this.bundles = new HashMap<Locale, Properties>(bundleFiles.size());
				for (final File bundle : bundleFiles) {
					analyzeBundleFile(bundle);
				}
			}
		}

		return this.bundles;
	}

	private Collection<File> getBundleFiles() {
		final File[] filesInBundleDirectory = this.bundlesDirectory.listFiles();
		LOG.trace(String.format("%s files in bundlesDirectory (%s)", filesInBundleDirectory.length, bundlesDirectory));
		List<File> bundleFiles = new LinkedList<File>();

		for (final File file : filesInBundleDirectory) {
			final Matcher bundleNameMatcher = this.bundleFilenamePattern.matcher(file.getName());
			if (bundleNameMatcher.matches()) {
				bundleFiles.add(file);
			} else {
				LOG.debug(String.format("Not messages file found : %s", file));
			}
		}

		return bundleFiles;
	}

	private final String finalToDebug() {
		final StringBuilder debugStrBuilder = new StringBuilder();
		debugStrBuilder.append(String.format("%s - ", getClass().getSimpleName()));
		debugStrBuilder.append(String.format("bundlesDirectory = %s - ", this.bundlesDirectory));
		debugStrBuilder.append(String.format("bundleNameFormat = %s - ", this.bundleNameFormat));
		debugStrBuilder.append(String.format("defaultLocale = %s ", this.defaultLocale));
		return debugStrBuilder.toString();
	}

	private void analyzeBundleFile(final File bundleFile) {
		final Matcher bundleNameMatcher = this.bundleFilenamePattern.matcher(bundleFile.getName());
		bundleNameMatcher.matches();

		final StringBuilder sLocaleBuilder = new StringBuilder();
		for (int i = 1; i <= bundleNameMatcher.groupCount(); i++) {
			if (bundleNameMatcher.group(i) != null) {
				sLocaleBuilder.append(bundleNameMatcher.group(i));
			}
		}

		LOG.trace(String.format("Found bundle for locale %s ", sLocaleBuilder));
		try {
			final Locale locale = LocaleUtils.toLocale(sLocaleBuilder.toString());
			final Properties prop = loadBundle(bundleFile);
			this.bundles.put(locale, prop);
		} catch (IllegalArgumentException e) {
			LOG.warn(String.format("Invalid bundle locale found : %s", e, sLocaleBuilder));
		}
	}

	private Properties getMessages(final Locale locale, final boolean loadDefaultOnNotFound) {
		Properties bundle;
		synchronized (lock) {
			bundle = selectCompatibleBundle(locale);
			if (bundle == null) {
				LOG.warn(String.format("No bundle found for locale %s", locale));
				if (loadDefaultOnNotFound && !locale.equals(defaultLocale)) {
					LOG.warn(String.format("Using %s", defaultLocale));
					bundle = getMessages(defaultLocale);
				}
			}
		}

		if (bundle == null) {
			final String folderIndication = String.format("(Directory: %s)", bundlesDirectory.getAbsolutePath());
			final String msg = String.format("No bundle found for locale %s %s - Please provide default locale", locale,
					folderIndication);

			throw new IllegalArgumentException(msg);
		}

		return bundle;
	}

	private Properties loadBundle(final File file) {
		synchronized (lock) {
			return PropertiesHelper.loadPropertiesFromFile(file);
		}
	}

	private void saveAllMessages(final Collection<Locale> locales) throws IOException {
		IOException thrown = null;
		for (final Locale locale : locales) {
			try {
				saveMessages(locale);
			} catch (IOException e) {
				LOG.error(String.format("Error saving %s bundle - Trying to continue", locale));
				if (thrown == null) {
					thrown = e;
				}
			}
		}
		if (thrown != null) {
			throw thrown;
		}
	}

	private Properties selectCompatibleBundle(final Locale locale) {
		Properties bundle = null;

		final Map<Locale, Properties> messages = getAllBundles();
		LOG.trace(String.format("Messages : %s", messages));

		// Recherche avec locale complete type en_US_<Variant>
		if (StringUtils.isNotBlank(locale.getVariant())) {
			bundle = messages.get(locale);
		}

		// Recherche fichier avec locale standard type en_US
		if (bundle == null && StringUtils.isNotBlank(locale.getCountry())) {
			bundle = messages.get(new Locale(locale.getLanguage(), locale.getCountry()));
		}

		// Recherche fichier avec locale globale type en
		if (bundle == null && StringUtils.isNotBlank(locale.getLanguage())) {
			bundle = messages.get(new Locale(locale.getLanguage()));
		}

		LOG.trace(String.format("Bundle %s : %s", locale, bundle));

		return bundle;
	}

	private File createCompressedLabelsFile() throws IOException {
		final File zipFile = File.createTempFile(RandomStringUtils.randomAlphanumeric(5), ".zip");
		LOG.info(String.format("Creating compressed file: %s", zipFile.getAbsolutePath()));
		return zipFile;
	}

	private void writeBundleToZip(final ZipOutputStream zipOut, final File bundleFile) throws IOException {
		synchronized (lock) {
			zipOut.putNextEntry(new ZipEntry(bundleFile.getName()));
			try (InputStream bundleInput = new FileInputStream(bundleFile)) {
				int len;
				final byte[] buf = new byte[1024];
				while ((len = bundleInput.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}

			} finally {
				zipOut.closeEntry();
			}
		}
	}
}
