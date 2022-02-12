package autumn.config.client.context.refresh;

import autumn.config.client.context.autoconfigure.RefreshProperties;
import autumn.config.client.context.scope.refresh.RefreshScope;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.StandardEnvironment;

public class LegacyContextRefresher extends ContextRefresher {

    public LegacyContextRefresher(ConfigurableApplicationContext context,
                                  RefreshScope scope,
                                  RefreshProperties properties) {
        super(context, scope, properties);
    }

    @Override
    protected void updateEnvironment() {
        addConfigFilesToEnvironment();
    }

    ConfigurableApplicationContext addConfigFilesToEnvironment() {
        StandardEnvironment environment = copyEnvironment(getContext().getEnvironment());
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Empty.class).bannerMode(Banner.Mode.OFF)
                .web(WebApplicationType.NONE).environment(environment);
        ConfigurableApplicationContext capture = builder.run();
        return capture;
    }
}
