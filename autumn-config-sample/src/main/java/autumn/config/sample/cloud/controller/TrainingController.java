package autumn.config.sample.cloud.controller;

import autumn.config.client.context.refresh.ContextRefresher;
import autumn.config.sample.cloud.config.BizConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
public class TrainingController {

    @Autowired
    private BizConfig bizConfig;
    @Autowired
    private ContextRefresher contextRefresher;

    @GetMapping("/greeting")
    public String greeting(String name) {
        return "Hello www " + name;
    }

    @GetMapping("/review")
    public String review() {
        return bizConfig.toString();
    }

    @GetMapping("/refresh")
    public String refresh() {
        contextRefresher.refresh();
        return bizConfig.toString();
    }

}
