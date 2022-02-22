package autumn.config.client;

import autumn.config.commons.ConfigDataMissingEnvironmentPostProcessor;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.core.env.Environment;

public class ConfigServerConfigDataMissingEnvironmentPostProcessor extends ConfigDataMissingEnvironmentPostProcessor {

    /**
     * Order of post processor, set to run after
     * {@link ConfigDataEnvironmentPostProcessor}.
     */
    public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1000;

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    @Override
    protected boolean shouldProcessEnvironment(Environment environment) {
        // don't run if using bootstrap or legacy processing
        if (bootstrapEnabled(environment) || useLegacyProcessing(environment)) {
            return false;
        }
        boolean configEnabled = environment.getProperty(ConfigClientProperties.PREFIX + ".enabled", Boolean.class,
                true);
        boolean importCheckEnabled = environment.getProperty(ConfigClientProperties.PREFIX + ".import-check.enabled",
                Boolean.class, true);
        if (!configEnabled || !importCheckEnabled) {
            return false;
        }
        return true;
    }

    static class ImportExceptionFailureAnalyzer extends AbstractFailureAnalyzer<ImportException> {

        @Override
        protected FailureAnalysis analyze(Throwable rootFailure, ImportException cause) {
            String description;
            if (cause.missingPrefix) {
                description = "The spring.config.import property is missing a " + PREFIX + " entry";
            }
            else {
                description = "No spring.config.import property has been defined";
            }
            String action = "Add a spring.config.import=configserver: property to your configuration.\n"
                    + "\tIf configuration is not required add spring.config.import=optional:configserver: instead.\n"
                    + "\tTo disable this check, set spring.cloud.config.enabled=false or \n"
                    + "\tspring.cloud.config.import-check.enabled=false.";
            return new FailureAnalysis(description, action, cause);
        }

    }

}
