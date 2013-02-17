package net.thucydides.core.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.common.annotations.VisibleForTesting;

public class Internationalization {
	
	private static final String BASE_DIRECTORY = "i18n";
	
	private static final String CAPABILITIES = "home";
	private static final String COVERAGE = "coverage";
	private static final String DEFAULT = "default";
	private static final String HISTORY = "history";
	private static final String HOME = "home";
	private static final String PROGRESS = "progress";
	private static final String PROGRESS_REPORT = "progress-report";
	private static final String RESULTS_BY_TAGTYPE = "results-by-tagtype";
	private static final String SCREENSHOT = "screenshot";
	private static final String SCREENSHOTS = "screenshots";
	private static final String TREEMAP = "treemap";
	
	private static List<String> propertiesFiles = Arrays.asList(
		CAPABILITIES, COVERAGE, DEFAULT, HISTORY, HOME, PROGRESS,
		PROGRESS_REPORT, RESULTS_BY_TAGTYPE, SCREENSHOT, SCREENSHOTS, TREEMAP
	);

	private String localeDirectory;
	private List<ResourceBundle> resourceBundles = new ArrayList<ResourceBundle>();
	private Map<String, String> bundlesAsMap = new HashMap<String, String>();

	public Internationalization(Locale locale) {
		final String localeString = locale.toString().toLowerCase();
		this.localeDirectory = BASE_DIRECTORY + "." + localeString + ".";

		for (String propertiesFile : propertiesFiles) {
			final String file = this.localeDirectory + propertiesFile;
			try {
				this.resourceBundles.add(ResourceBundle.getBundle(file, locale));
			} catch (MissingResourceException e) {
				throw new InternationalizationException(e);
			}
		}

		bundlesToMap();
	}

	public Internationalization() {
		this(Locale.getDefault());
	}

	public Map<String, String> getBundlesAsMap() {
		return this.bundlesAsMap;
	}

	@VisibleForTesting
	public List<ResourceBundle> getResourceBundles() {
		return resourceBundles;
	}

	@VisibleForTesting
	public String getLocaleDirectory() {
		return this.localeDirectory;
	}

	protected void bundlesToMap() {
		for (ResourceBundle bundle : getResourceBundles()) {
			for (String key : bundle.keySet()) {
				this.bundlesAsMap.put(key, bundle.getString(key));
			}
		}
	}
}
