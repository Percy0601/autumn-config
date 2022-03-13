package autumn.config.spi;

import autumn.config.build.AutumnInjector;
import autumn.config.internals.ConfigFactoryManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultConfigFactoryManager implements ConfigFactoryManager {
    private ConfigRegistry m_registry;

    private Map<String, ConfigFactory> m_factories = new ConcurrentHashMap<>();

    public DefaultConfigFactoryManager() {
        m_registry = AutumnInjector.getInstance(ConfigRegistry.class);
    }

    @Override
    public ConfigFactory getFactory(String namespace) {
        // step 1: check hacked factory
        ConfigFactory factory = m_registry.getFactory(namespace);

        if (factory != null) {
            return factory;
        }

        // step 2: check cache
        factory = m_factories.get(namespace);

        if (factory != null) {
            return factory;
        }

        // step 3: check declared config factory
        factory = AutumnInjector.getInstance(ConfigFactory.class);

        if (factory != null) {
            return factory;
        }

        // step 4: check default config factory
        factory = AutumnInjector.getInstance(ConfigFactory.class);

        m_factories.put(namespace, factory);

        // factory should not be null
        return factory;
    }
}
