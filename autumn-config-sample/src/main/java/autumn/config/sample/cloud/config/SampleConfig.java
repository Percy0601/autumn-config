package autumn.config.sample.cloud.config;

import autumn.config.context.autoconfigure.RefreshProperties;
import autumn.config.context.properties.ConfigurationPropertiesBeans;
import autumn.config.context.properties.ConfigurationPropertiesRebinder;
import autumn.config.context.refresh.LegacyContextRefresher;
import autumn.config.context.scope.refresh.RefreshScope;
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
