package autumn.config.client.context.refresh;

import autumn.config.client.context.autoconfigure.RefreshProperties;
import autumn.config.client.context.environment.EnvironmentChangeEvent;
import autumn.config.client.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;
import org.springframework.util.CollectionUtils;

import java.util.*;

public abstract class ContextRefresher {
    protected static final String[] DEFAULT_PROPERTY_SOURCES = new String[] {
            // order matters, if cli args aren't first, things get messy
            CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME, "defaultProperties" };

    protected Set<String> standardSources = new HashSet<>(
            Arrays.asList(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    "configurationProperties"));

    protected List<String> additionalPropertySourcesToRetain = new ArrayList<>();

    private ConfigurableApplicationContext context;

    private RefreshScope scope;

    protected ContextRefresher(ConfigurableApplicationContext context,
                               RefreshScope scope,
                               RefreshProperties properties) {
        this.context = context;
        this.scope = scope;
        additionalPropertySourcesToRetain = properties.getAdditionalPropertySourcesToRetain();
    }

    protected ConfigurableApplicationContext getContext() {
        return this.context;
    }

    protected RefreshScope getScope() {
        return this.scope;
    }

    public synchronized Set<String> refresh() {
        Set<String> keys = refreshEnvironment();
        this.scope.refreshAll();
        return keys;
    }

    public synchronized Set<String> refreshEnvironment() {
        Map<String, Object> before = extract(this.context.getEnvironment().getPropertySources());
        updateEnvironment();
        Set<String> keys = changes(before, extract(this.context.getEnvironment().getPropertySources())).keySet();
        this.context.publishEvent(new EnvironmentChangeEvent(this.context, keys));
        return keys;
    }

    protected abstract void updateEnvironment();

    // Don't use ConfigurableEnvironment.merge() in case there are clashes with property
    // source names
    protected StandardEnvironment copyEnvironment(ConfigurableEnvironment input) {
        StandardEnvironment environment = new StandardEnvironment();
        MutablePropertySources capturedPropertySources = environment.getPropertySources();
        // Only copy the default property source(s) and the profiles over from the main
        // environment (everything else should be pristine, just like it was on startup).
        List<String> propertySourcesToRetain = new ArrayList<>(Arrays.asList(DEFAULT_PROPERTY_SOURCES));
        if (!CollectionUtils.isEmpty(additionalPropertySourcesToRetain)) {
            propertySourcesToRetain.addAll(additionalPropertySourcesToRetain);
        }

        for (String name: propertySourcesToRetain) {
            if (input.getPropertySources().contains(name)) {
                if (capturedPropertySources.contains(name)) {
                    capturedPropertySources.replace(name, input.getPropertySources().get(name));
                }
                else {
                    capturedPropertySources.addLast(input.getPropertySources().get(name));
                }
            }
        }
        environment.setActiveProfiles(input.getActiveProfiles());
        environment.setDefaultProfiles(input.getDefaultProfiles());
        return environment;
    }

    private Map<String, Object> changes(Map<String, Object> before, Map<String, Object> after) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                result.put(key, null);
            }
            else if (!equal(before.get(key), after.get(key))) {
                result.put(key, after.get(key));
            }
        }
        for (String key : after.keySet()) {
            if (!before.containsKey(key)) {
                result.put(key, after.get(key));
            }
        }
        return result;
    }

    private boolean equal(Object one, Object two) {
        if (one == null && two == null) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equals(two);
    }

    private Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<>();
        List<PropertySource<?>> sources = new ArrayList<>();
        for (PropertySource<?> source : propertySources) {
            sources.add(0, source);
        }
        for (PropertySource<?> source : sources) {
            if (!this.standardSources.contains(source.getName())) {
                extract(source, result);
            }
        }
        return result;
    }

    private void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource) {
            try {
                List<PropertySource<?>> sources = new ArrayList<>();
                for (PropertySource<?> source : ((CompositePropertySource) parent).getPropertySources()) {
                    sources.add(0, source);
                }
                for (PropertySource<?> source : sources) {
                    extract(source, result);
                }
            }
            catch (Exception e) {
                return;
            }
        } else if (parent instanceof EnumerablePropertySource) {
            for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
                result.put(key, parent.getProperty(key));
            }
        }
    }

    @Configuration(proxyBeanMethods = false)
    protected static class Empty {

    }

}
