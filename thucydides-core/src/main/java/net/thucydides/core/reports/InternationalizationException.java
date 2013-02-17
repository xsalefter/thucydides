package net.thucydides.core.reports;

import java.util.MissingResourceException;

public class InternationalizationException extends MissingResourceException {

	private static final long serialVersionUID = 3707306111672195562L;

	public InternationalizationException(MissingResourceException mre) {
		super(mre.getMessage(), mre.getClassName(), mre.getKey());
	}
}
