package cc.viridian.service.statement.service;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.model.Statement;
import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.repository.StatementProducer;
import cc.viridian.service.statement.repository.UpdateJobProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ProcessJobService {

    private StatementProducer statementProducer;

    private UpdateJobProducer updateJobProducer;

    @Autowired
    public ProcessJobService(StatementProducer statementProducer, UpdateJobProducer updateJobProducer) {
        this.statementProducer = statementProducer;
        this.updateJobProducer = updateJobProducer;
    }

    public Boolean process(JobTemplate data) {
        log.info("process getStatement for : " + data.getAccount() + " " + data.getCoreBankAdapter());

        CoreBankProvider coreBank = CoreBankProvider.getInstance();
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now().minusDays(1);
        Statement statement = coreBank.getStatement(data.getAccount(), data.getType(), data.getCurrency(), from, to );

        log.debug("get statement from Corebank adapter " + coreBank.getClass().getName());
        log.debug(statement.toString());

        //send the statement to sender queue
        statementProducer.send(data.getId().toString(), statement);

        //send updates to update queue
        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCoreBankAdapter());
        updateJob.setErrorCode( "0" ); //todo:
        updateJob.setErrorDesc("no error"); //todo:
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setRetryNumber(0); //todo:

        updateJobProducer.send(data.getId().toString(), updateJob);

        return true;
    }
}
