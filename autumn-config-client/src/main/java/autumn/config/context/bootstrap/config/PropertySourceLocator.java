package autumn.config.context.bootstrap.config;


import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.*;

/**
 * Strategy for locating (possibly remote) property sources for the Environment.
 * Implementations should not fail unless they intend to prevent the application from
 * starting.
 *
 * @author Dave Syer
 *
 */
public interface PropertySourceLocator {

    /**
     * @param environment The current Environment.
     * @return A PropertySource, or null if there is none.
     * @throws IllegalStateException if there is a fail-fast condition.
     */
    PropertySource<?> locate(Environment environment);

    default Collection<PropertySource<?>> locateCollection(Environment environment) {
        return locateCollection(this, environment);
    }

    static Collection<PropertySource<?>> locateCollection(PropertySourceLocator locator, Environment environment) {
        PropertySource<?> propertySource = locator.locate(environment);
        if (propertySource == null) {
            return Collections.emptyList();
        }
        if (CompositePropertySource.class.isInstance(propertySource)) {
            Collection<PropertySource<?>> sources = ((CompositePropertySource) propertySource).getPropertySources();
            List<PropertySource<?>> filteredSources = new ArrayList<>();
            for (PropertySource<?> p : sources) {
                if (p != null) {
                    filteredSources.add(p);
                }
            }
            return filteredSources;
        }
        else {
            return Arrays.asList(propertySource);
        }
    }

}
