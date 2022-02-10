package autumn.config.sample.cloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@RefreshScope
@Component
public class ValueConfig {
    @Value("${rest.uuid}")
    private String uuid;
}
