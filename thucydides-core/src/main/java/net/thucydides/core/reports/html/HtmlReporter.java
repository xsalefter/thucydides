package net.thucydides.core.reports.html;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.reports.Internationalization;
import net.thucydides.core.reports.templates.ReportTemplate;
import net.thucydides.core.reports.templates.TemplateManager;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * An HTML report generates reports in a given directory and uses resources (images,...) from another.
 *
 * @author johnsmart
 */
public abstract class HtmlReporter {

    private static final String DEFAULT_RESOURCE_DIRECTORY = "report-resources";
    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;
    private File outputDirectory;
    private final TemplateManager templateManager;
    private final EnvironmentVariables environmentVariables;

    private Internationalization internationalization;

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReporter.class);

    public HtmlReporter() {
        this(Injectors.getInjector().getInstance(EnvironmentVariables.class));
        this.internationalization = new Internationalization();
    }

    public HtmlReporter(final EnvironmentVariables environmentVariables) {
        super();
        this.templateManager = Injectors.getInjector().getInstance(TemplateManager.class);
        this.environmentVariables = environmentVariables;
        String language = environmentVariables.getProperty(ThucydidesSystemProperty.TEST_REPORT_LANGUAGE);
        if (language.isEmpty()) {
        	this.internationalization = new Internationalization();
        } else {
        	this.internationalization = new Internationalization(new Locale(language));
        }
    }

    private TemplateManager getTemplateManager() {
        return templateManager;
    }

    /**
     * HTML reports will be generated here.
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Resources such as CSS stylesheets or images.
     */
    public void setResourceDirectory(final String resourceDirectory) {
        this.resourceDirectory = resourceDirectory;
    }

    public String getResourceDirectory() {
        return resourceDirectory;
    }

    protected EnvironmentVariables getEnvironmentVariables() {
        return environmentVariables;
    }

    private boolean alreadyCopied = false;

    protected void copyResourcesToOutputDirectory() throws IOException {
        if (!alreadyCopied) {
            updateResourceDirectoryFromSystemPropertyIfDefined();
            HtmlResourceCopier copier = new HtmlResourceCopier(getResourceDirectory());

            copier.copyHTMLResourcesTo(getOutputDirectory());
            alreadyCopied = true;
        }
    }

    private void updateResourceDirectoryFromSystemPropertyIfDefined() {

        String systemDefinedResourceDirectory
             = getEnvironmentVariables().getProperty(ThucydidesSystemProperty.REPORT_RESOURCE_PATH.getPropertyName());
        if (systemDefinedResourceDirectory != null) {
            setResourceDirectory(systemDefinedResourceDirectory);
        }
    }

    /**
     * Write the actual HTML report to a file with the specified name in the output directory.
     */
    protected File writeReportToOutputDirectory(final String reportFilename,
                                                final String htmlContents) throws IOException {
        File report = new File(getOutputDirectory(), reportFilename);
        FileUtils.writeStringToFile(report, htmlContents);
        LOGGER.debug("Writing HTML report to {}", report.getAbsolutePath());
        return report;
    }

    protected Merger mergeTemplate(final String templateFile) {
        return new Merger(templateFile);
    }

    protected class Merger {
        final String templateFile;

        public Merger(final String templateFile) {
            this.templateFile = templateFile;
        }

        public String usingContext(final Map<String, Object> context) {
            try {
            	context.putAll(internationalization.getBundlesAsMap());
                ReportTemplate template = getTemplateManager().getTemplateFrom(templateFile);
                StringWriter sw = new StringWriter();
                template.merge(context, sw);
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Failed to merge template: " + e.getMessage(), e);
            }
        }
    }

}