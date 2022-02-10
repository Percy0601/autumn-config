package autumn.config.sample.cloud.controller;

import autumn.config.sample.cloud.config.BizConfig;
import autumn.config.sample.cloud.config.ValueConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh")
public class SpringCloudRefreshController {
    @Autowired
    private ContextRefresher contextRefresher;
    @Autowired
    private BizConfig bizConfig;
    @Autowired
    private ValueConfig valueConfig;

    @GetMapping(path = "/show")
    public String show() {
        return valueConfig.toString();
    }

    @GetMapping(path = "/refresh")
    public String refresh() {
        new Thread(() -> contextRefresher.refresh()).start();
        return show();
    }

}
