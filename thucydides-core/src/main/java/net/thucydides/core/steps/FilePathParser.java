package net.thucydides.core.steps;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

/**
 * Builds a file path by substituting environment variables.
 * Supported environment variables include $HOME, $USERDIR and $DATADIR.
 * $DATADIR is provided by setting the thucydides.data.dir environment property.
 */
public class FilePathParser {
    private final EnvironmentVariables environmentVariables;

    public FilePathParser(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String getInstanciatedPath(String path) {
        if (path == null) {
            return path;
        }
        String testDataSource = operatingSystemLocalized(path);
        testDataSource = injectVariable(testDataSource, "HOME", System.getProperty("user.home"));
        testDataSource = injectVariable(testDataSource, "USERDIR", System.getProperty("user.dir"));
        testDataSource = injectVariable(testDataSource, "DATADIR",
                                        ThucydidesSystemProperty.DATA_DIRECTORY.from(environmentVariables));

        return testDataSource;
    }

    private String operatingSystemLocalized(String testDataSource) {
        return StringUtils.replace(testDataSource, getFileSeparatorToReplace(), getFileSeparator());
    }

    private String injectVariable(String path, String variable, String directory) {
        if (StringUtils.isNotEmpty(directory)) {
            path = StringUtils.replace(path, "$" + variable, directory);
            return StringUtils.replace(path, "${" + variable + "}", directory);
        } else {
            return path;
        }
    }

    protected String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    protected String getFileSeparatorToReplace() {
        return (getFileSeparator().equals("/")) ? "\\" : "/";
    }
}
