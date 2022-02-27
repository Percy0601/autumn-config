package autumn.config.client;

import autumn.config.context.bootstrap.config.PropertySourceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertySource;

import java.util.Collection;

/**
 * @author Dave Syer
 * @author Mathieu Ouellet
 *
 */
@Order(0)
public class ConfigServicePropertySourceLocator implements PropertySourceLocator {

    private static Log logger = LogFactory.getLog(ConfigServicePropertySourceLocator.class);

    private ConfigClientProperties defaultProperties;

    public ConfigServicePropertySourceLocator(ConfigClientProperties defaultProperties) {
        this.defaultProperties = defaultProperties;
    }

    @Override
    public org.springframework.core.env.PropertySource<?> locate(org.springframework.core.env.Environment environment) {
        ConfigClientProperties properties = this.defaultProperties.override(environment);

        return null;
    }

    @Override
    public Collection<PropertySource<?>> locateCollection(
            org.springframework.core.env.Environment environment) {
        return PropertySourceLocator.locateCollection(this, environment);
    }

}