package cc.viridian.service.statement.controller;

import cc.viridian.service.statement.config.CorebankAdapterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    CorebankAdapterConfig corebankAdapterConfig;

    @RequestMapping("/")
    public String home() {
        return "viridian service-statement-get version " + corebankAdapterConfig.getApplicationVersion();
    }

}
