package autumn.config.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/training")
public class TrainingController {

    @GetMapping("/greeting")
    public String greeting(String name) {
        return "Hello www " + name;
    }


}
