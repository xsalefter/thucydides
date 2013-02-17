package net.thucydides.core.reports;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class InternationalizationTest {

	@Test
	public void when_default_constructor_invoked() {
		Internationalization i18n = new Internationalization();
		assertThat(i18n, is(notNullValue()));
		assertThat(i18n.getLocaleDirectory(), is("i18n.en_us."));
	}
	
	@Test
	public void when_constructor_with_locale_args_invoked() {
		Internationalization i18n = new Internationalization(new Locale("in_id"));
		assertThat(i18n, is(notNullValue()));
		assertThat(i18n.getLocaleDirectory(), is("i18n.in_id."));
	}

	@Test(expected=InternationalizationException.class)
	public void when_constructor_invoked_but_locale_undefined() {
		new Internationalization(Locale.PRC);
	}

	@Test
	public void get_bundle_as_map_test() {
		Internationalization i18n = new Internationalization();
		// In test environment, we only have message-bundle in home.properties.
		Map<String, String> bundles = i18n.getBundlesAsMap();
		assertThat(bundles.size(), is(3));
		assertThat(bundles.get("i18n_home_test_passed"), is("tests passed"));
	}

	@Test
	public void get_resource_bundles_test() {
		Internationalization i18n = new Internationalization();
		List<ResourceBundle> bundles = i18n.getResourceBundles();
		assertThat(bundles, is(notNullValue()));
		assertThat(bundles.size(), is(11)); // Should be 11.
	}

	@Test
	public void get_locale_directory_test() {
		Internationalization i18n = new Internationalization();
		String dirName = i18n.getLocaleDirectory();
		assertThat(dirName, is(not("")));
		assertThat(dirName, is("i18n.en_us."));
	}
}
