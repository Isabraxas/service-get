package cc.viridian.service.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableScheduling
@Slf4j
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);

		//sandbox
        /*
        Statement statement = ScheduleService.getInstance().getRandomStatement();

        System.out.println( statement);
        System.out.println( statement.getHeader());
        System.out.println( statement.getDetails());
        */
    }

}
