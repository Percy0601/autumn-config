package autumn.config.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ryan Baxter
 */
// TODO: 4.0.0 move to org.springframework.cloud.commons.config
public abstract class ConfigDataMissingEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final Bindable<String[]> CONFIG_DATA_LOCATION_ARRAY = Bindable.of(String[].class);

    /**
     * Order of post processor, set to run after
     * {@link ConfigDataEnvironmentPostProcessor}.
     */
    public static final int ORDER = ConfigDataEnvironmentPostProcessor.ORDER + 1000;

    private final Logger LOG = LoggerFactory.getLogger(ConfigDataMissingEnvironmentPostProcessor.class);

    @Override
    public int getOrder() {
        return ORDER;
    }

    protected abstract boolean shouldProcessEnvironment(Environment environment);

    protected abstract String getPrefix();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!shouldProcessEnvironment(environment)) {
            return;
        }
        List<Object> property = getConfigImports(environment);
        if (property == null || property.isEmpty()) {
            throw new ImportException("No spring.config.import set", false);
        }
        if (!property.stream().anyMatch(impt -> ((String) impt).contains(getPrefix()))) {
            throw new ImportException("spring.config.import missing " + getPrefix(), true);
        }
    }

    private List<Object> getConfigImports(ConfigurableEnvironment environment) {
        MutablePropertySources propertySources = environment.getPropertySources();

        return new ArrayList<>();
    }



    public static class ImportException extends RuntimeException {
        public final boolean missingPrefix;

        ImportException(String message, boolean missingPrefix) {
            super(message);
            this.missingPrefix = missingPrefix;
        }

    }

}
