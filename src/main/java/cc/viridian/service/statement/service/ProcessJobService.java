package cc.viridian.service.statement.service;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.model.Statement;
import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.repository.StatementProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@Slf4j
public class ProcessJobService {

    @Autowired
    private StatementProducer statementProducer;

    public Boolean process(JobTemplate data) {
        log.info(data.getAccount());
        log.info(data.getCoreBankAdapter());

        CoreBankProvider coreBank = CoreBankProvider.getInstance();
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now().minusDays(1);
        Statement statement = coreBank.getStatement(data.getAccount(), data.getType(), data.getCurrency(), from, to );

        log.info("get statement from Corebank adapter " + coreBank.getClass().getName());
        log.debug(statement.toString());

        statementProducer.send(data.getId().toString(), statement);

        return true;
    }
}
