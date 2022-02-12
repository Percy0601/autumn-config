package autumn.config.sample.cloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "biz")
public class BizConfig {
    private String key;
    private Long refresh;
}
