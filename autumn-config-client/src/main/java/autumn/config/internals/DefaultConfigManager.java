package autumn.config.internals;

import autumn.config.Config;
import autumn.config.ConfigFile;
import autumn.config.build.AutumnInjector;
import autumn.config.core.enums.ConfigFileFormat;
import autumn.config.spi.ConfigFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultConfigManager implements ConfigManager{
    private ConfigFactoryManager m_factoryManager;

    private Map<String, Config> m_configs = new ConcurrentHashMap<>();
    private Map<String, ConfigFile> m_configFiles = new ConcurrentHashMap();

    public DefaultConfigManager() {
        m_factoryManager = AutumnInjector.getInstance(ConfigFactoryManager.class);
    }

    @Override
    public Config getConfig(String namespace) {
        Config config = m_configs.get(namespace);

        if (config == null) {
            synchronized (this) {
                config = m_configs.get(namespace);

                if (config == null) {
                    ConfigFactory factory = m_factoryManager.getFactory(namespace);

                    config = factory.create(namespace);
                    m_configs.put(namespace, config);
                }
            }
        }

        return config;
    }

    @Override
    public ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        String namespaceFileName = String.format("%s.%s", namespace, configFileFormat.getValue());
        ConfigFile configFile = m_configFiles.get(namespaceFileName);

        if (configFile == null) {
            synchronized (this) {
                configFile = m_configFiles.get(namespaceFileName);
                if (configFile == null) {
                    ConfigFactory factory = m_factoryManager.getFactory(namespaceFileName);
                    configFile = factory.createConfigFile(namespaceFileName, configFileFormat);
                    m_configFiles.put(namespaceFileName, configFile);
                }
            }
        }

        return configFile;
    }
}
