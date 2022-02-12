package autumn.config.sample.cloud.config;

import autumn.config.client.context.autoconfigure.RefreshProperties;
import autumn.config.client.context.properties.ConfigurationPropertiesBeans;
import autumn.config.client.context.properties.ConfigurationPropertiesRebinder;
import autumn.config.client.context.refresh.LegacyContextRefresher;
import autumn.config.client.context.scope.refresh.RefreshScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleConfig {


    @Bean
    @ConditionalOnMissingBean(RefreshScope.class)
    public static RefreshScope refreshScope() {
        return new RefreshScope();
    }

    @Bean
    public RefreshProperties refreshProperties() {
        return new RefreshProperties();
    }

    @Bean
    public LegacyContextRefresher LegacyContextRefresher(ConfigurableApplicationContext context,
                                                         RefreshScope scope,
                                                         RefreshProperties properties) {
        return new LegacyContextRefresher(context, scope, properties);
    }

    @Bean
    public ConfigurationPropertiesRebinder configurationPropertiesRebinder(ConfigurationPropertiesBeans configurationPropertiesBeans) {
        return new ConfigurationPropertiesRebinder(configurationPropertiesBeans);
    }

    @Bean
    public ConfigurationPropertiesBeans configurationPropertiesBeans() {
        return new ConfigurationPropertiesBeans();
    }

}
