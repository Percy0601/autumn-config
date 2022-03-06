package autumn.config;

import autumn.config.build.ApplicationContextAwareUtil;
import autumn.config.core.ConfigConsts;
import autumn.config.core.enums.ConfigFileFormat;
import autumn.config.internals.ConfigManager;
import autumn.config.spi.ConfigFactory;
import autumn.config.spi.ConfigRegistry;

public class ConfigService {

    private static final ConfigService s_instance = new ConfigService();

    private volatile ConfigManager m_configManager;
    private volatile ConfigRegistry m_configRegistry;

    private ConfigManager getManager() {
        if (m_configManager == null) {
            synchronized (this) {
                if (m_configManager == null) {
                    m_configManager = ApplicationContextAwareUtil.getBean(ConfigManager.class);
                }
            }
        }

        return m_configManager;
    }

    private ConfigRegistry getRegistry() {
        if (m_configRegistry == null) {
            synchronized (this) {
                if (m_configRegistry == null) {
                    m_configRegistry = ApplicationContextAwareUtil.getBean(ConfigRegistry.class);
                }
            }
        }

        return m_configRegistry;
    }

    /**
     * Get Application's config instance.
     *
     * @return config instance
     */
    public static Config getAppConfig() {
        return getConfig(ConfigConsts.NAMESPACE_APPLICATION);
    }

    /**
     * Get the config instance for the namespace.
     *
     * @param namespace the namespace of the config
     * @return config instance
     */
    public static Config getConfig(String namespace) {
        return s_instance.getManager().getConfig(namespace);
    }

    public static ConfigFile getConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        return s_instance.getManager().getConfigFile(namespace, configFileFormat);
    }

    static void setConfig(Config config) {
        setConfig(ConfigConsts.NAMESPACE_APPLICATION, config);
    }

    /**
     * Manually set the config for the namespace specified, use with caution.
     *
     * @param namespace the namespace
     * @param config    the config instance
     */
    static void setConfig(String namespace, final Config config) {
        s_instance.getRegistry().register(namespace, new ConfigFactory() {
            @Override
            public Config create(String namespace) {
                return config;
            }

            @Override
            public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
                return null;
            }

        });
    }

    static void setConfigFactory(ConfigFactory factory) {
        setConfigFactory(ConfigConsts.NAMESPACE_APPLICATION, factory);
    }

    /**
     * Manually set the config factory for the namespace specified, use with caution.
     *
     * @param namespace the namespace
     * @param factory   the factory instance
     */
    static void setConfigFactory(String namespace, ConfigFactory factory) {
        s_instance.getRegistry().register(namespace, factory);
    }

}
