package cc.viridian.service.statement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleTask {

    /**
     * Placeholder for scheduler, currently is not used.
     */
    //@Scheduled(cron = "0 * * * * ?")  //each minute at 0 seconds
    //@Scheduled(fixedDelay = 1000)
    public void scheduleTaskUsingCronExpression() {

        long now = System.currentTimeMillis() / 1000;
        System.out.println("Current Thread : " +  Thread.currentThread().getName());

        //Statement statement = ScheduleService.getInstance().getRandomStatement();

        //System.out.println( statement);
        //System.out.println( statement.getHeader());
        //System.out.println( statement.getDetails());

    }
}
