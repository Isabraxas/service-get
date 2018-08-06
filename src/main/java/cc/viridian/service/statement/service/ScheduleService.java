package cc.viridian.service.statement.service;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.model.Statement;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.Random;

@Slf4j
@NoArgsConstructor
public class ScheduleService {

    private static ScheduleService instance = new ScheduleService();

    public static ScheduleService getInstance() {
        return instance;
    }

    public Statement getRandomStatement() {
        CoreBankProvider coreBank = CoreBankProvider.getInstance();
        Random random = new Random();

        String account =  (random.nextInt(99999) + 100000 ) + "00" + (random.nextInt(10) + 10);
        String type = "cc";
        String currency = "bob";
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now().minusDays(1);
        Statement statement = coreBank.getStatement(account, type, currency, from, to );

        return statement;
    }
}
