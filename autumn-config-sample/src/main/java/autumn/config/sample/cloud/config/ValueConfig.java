package autumn.config.sample.cloud.config;

import autumn.config.context.config.annotation.RefreshScope;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@RefreshScope
@Component
public class ValueConfig {
    @Value("${rest.uuid}")
    private String uuid;

    @Value("${rest.uuid1}")
    private String uuid1;

    @Value("${rest.uuid2}")
    private String uuid2;

    @Value("${rest.uuid3}")
    private String uuid3;

    @Value("${rest.uuid4}")
    private String uuid4;

    @Value("${rest.uuid5}")
    private String uuid5;

    @Value("${rest.uuid6}")
    private String uuid6;

    @Value("${rest.uuid7}")
    private String uuid7;

    @Value("${rest.uuid8}")
    private String uuid8;

    @Value("${rest.uuid9}")
    private String uuid9;

    @Value("${rest.uuid10}")
    private String uuid10;

    @Value("${rest.uuid11}")
    private String uuid11;

    @Value("${rest.uuid12}")
    private String uuid12;

    @Value("${rest.uuid13}")
    private String uuid13;

    @Value("${rest.uuid14}")
    private String uuid14;

    @Value("${rest.uuid15}")
    private String uuid15;

    @Value("${rest.uuid16}")
    private String uuid16;

    @Value("${rest.uuid17}")
    private String uuid17;

    @Value("${rest.uuid18}")
    private String uuid18;

    @Value("${rest.uuid19}")
    private String uuid19;

    @Value("${rest.uuid20}")
    private String uuid20;


}
