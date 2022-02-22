package autumn.config.client;

import autumn.config.commons.ConfigDataMissingEnvironmentPostProcessor;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.core.env.Environment;

import static autumn.config.client.ConfigClientProperties.PREFIX;
import static autumn.config.context.util.PropertyUtils.bootstrapEnabled;
import static autumn.config.context.util.PropertyUtils.useLegacyProcessing;

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
        boolean configEnabled = environment.getProperty(PREFIX + ".enabled", Boolean.class,
                true);
        boolean importCheckEnabled = environment.getProperty(PREFIX + ".import-check.enabled",
                Boolean.class, true);
        if (!configEnabled || !importCheckEnabled) {
            return false;
        }
        return true;
    }

}
