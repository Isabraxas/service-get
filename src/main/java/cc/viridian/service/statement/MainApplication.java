package cc.viridian.service.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableScheduling
@Slf4j
public class MainApplication {

    /**
     * Main Application.
     *
     * @param args
     */
    public static void main(final String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
