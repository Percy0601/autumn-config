package autumn.config.spi;

import autumn.config.Config;
import autumn.config.ConfigFile;
import autumn.config.build.ApplicationContextAwareUtil;
import autumn.config.core.enums.ConfigFileFormat;
import autumn.config.internals.*;
import autumn.config.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link ConfigFactory}.
 * <p>
 * Supports namespaces of format:
 * <ul>
 *   <li>{@link ConfigFileFormat#Properties}</li>
 *   <li>{@link ConfigFileFormat#XML}</li>
 *   <li>{@link ConfigFileFormat#JSON}</li>
 *   <li>{@link ConfigFileFormat#YML}</li>
 *   <li>{@link ConfigFileFormat#YAML}</li>
 *   <li>{@link ConfigFileFormat#TXT}</li>
 * </ul>
 *
 * @author Jason Song(song_s@ctrip.com)
 * @author Diego Krupitza(info@diegokrupitza.com)
 */
public class DefaultConfigFactory implements ConfigFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigFactory.class);
    private final ConfigUtil m_configUtil;

    public DefaultConfigFactory() {
        m_configUtil = ApplicationContextAwareUtil.getBean(ConfigUtil.class);
    }

    @Override
    public Config create(String namespace) {
        ConfigFileFormat format = determineFileFormat(namespace);

        ConfigRepository configRepository = null;

        // although ConfigFileFormat.Properties are compatible with themselves we
        // should not create a PropertiesCompatibleFileConfigRepository for them
        // calling the method `createLocalConfigRepository(...)` is more suitable
        // for ConfigFileFormat.Properties
        if (ConfigFileFormat.isPropertiesCompatible(format) &&
                format != ConfigFileFormat.Properties) {
            configRepository = createPropertiesCompatibleFileConfigRepository(namespace, format);
        } else {
            configRepository = createConfigRepository(namespace);
        }

        logger.debug("Created a configuration repository of type [{}] for namespace [{}]",
                configRepository.getClass().getName(), namespace);

        return this.createRepositoryConfig(namespace, configRepository);
    }

    protected Config createRepositoryConfig(String namespace, ConfigRepository configRepository) {
        return new DefaultConfig(namespace, configRepository);
    }

    @Override
    public ConfigFile createConfigFile(String namespace, ConfigFileFormat configFileFormat) {
        ConfigRepository configRepository = createConfigRepository(namespace);
        switch (configFileFormat) {
            case Properties:
                return new PropertiesConfigFile(namespace, configRepository);
            case XML:
                return new XmlConfigFile(namespace, configRepository);
            case JSON:
                return new JsonConfigFile(namespace, configRepository);
            case TXT:
                return new TxtConfigFile(namespace, configRepository);
        }

        return null;
    }

    ConfigRepository createConfigRepository(String namespace) {
        if (m_configUtil.isPropertyFileCacheEnabled()) {
            return createLocalConfigRepository(namespace);
        }
        return createRemoteConfigRepository(namespace);
    }

    /**
     * Creates a local repository for a given namespace
     *
     * @param namespace the namespace of the repository
     * @return the newly created repository for the given namespace
     */
    LocalFileConfigRepository createLocalConfigRepository(String namespace) {
        if (m_configUtil.isInLocalMode()) {
            logger.warn(
                    "==== Apollo is in local mode! Won't pull configs from remote server for namespace {} ! ====",
                    namespace);
            return new LocalFileConfigRepository(namespace);
        }
        return new LocalFileConfigRepository(namespace, createRemoteConfigRepository(namespace));
    }

    RemoteConfigRepository createRemoteConfigRepository(String namespace) {
        return new RemoteConfigRepository(namespace);
    }

    PropertiesCompatibleFileConfigRepository createPropertiesCompatibleFileConfigRepository(
            String namespace, ConfigFileFormat format) {
        String actualNamespaceName = trimNamespaceFormat(namespace, format);
        PropertiesCompatibleConfigFile configFile = (PropertiesCompatibleConfigFile) ConfigService
                .getConfigFile(actualNamespaceName, format);

        return new PropertiesCompatibleFileConfigRepository(configFile);
    }

    // for namespaces whose format are not properties, the file extension must be present, e.g. application.yaml
    ConfigFileFormat determineFileFormat(String namespaceName) {
        String lowerCase = namespaceName.toLowerCase();
        for (ConfigFileFormat format : ConfigFileFormat.values()) {
            if (lowerCase.endsWith("." + format.getValue())) {
                return format;
            }
        }

        return ConfigFileFormat.Properties;
    }

    String trimNamespaceFormat(String namespaceName, ConfigFileFormat format) {
        String extension = "." + format.getValue();
        if (!namespaceName.toLowerCase().endsWith(extension)) {
            return namespaceName;
        }

        return namespaceName.substring(0, namespaceName.length() - extension.length());
    }

}