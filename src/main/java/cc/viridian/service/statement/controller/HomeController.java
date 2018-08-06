package cc.viridian.service.statement.controller;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @RequestMapping("/")
    public String home(
        @RequestParam(required = false) String localUrl,
        @RequestParam(required = false) String remoteUrl
    ) {

        return "viridian get-statement-service\n\n";
    }

}
