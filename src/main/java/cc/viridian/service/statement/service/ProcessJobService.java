package cc.viridian.service.statement.service;

import cc.viridian.provider.payload.GetStatementResponse;
import cc.viridian.provider.payload.ResponseErrorCode;
import cc.viridian.provider.spi.CoreBank;
import cc.viridian.service.statement.config.CorebankAdapterConfig;
import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.repository.SenderProducer;
import cc.viridian.service.statement.repository.UpdateJobProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ProcessJobService {

    private SenderProducer statementProducer = null;

    private UpdateJobProducer updateJobProducer;

    private CorebankAdapterConfig corebankAdapterConfig;

    @Autowired
    public ProcessJobService(SenderProducer statementProducer, UpdateJobProducer updateJobProducer,
                             CorebankAdapterConfig corebankAdapterConfig) {
        this.statementProducer = statementProducer;
        this.updateJobProducer = updateJobProducer;
        this.corebankAdapterConfig = corebankAdapterConfig;
    }

    public void process(final JobTemplate data) {
        log.info("process getStatement: " + data.getAccount() + " " + data.getCorebankAdapter()
            + " " + data.getDateFrom() + " " + data.getDateTo());

        CoreBank coreBank = corebankAdapterConfig.getCorebankAdapter(data.getCorebankAdapter());
        if (coreBank == null) {
            GetStatementResponse invalidAdapterResponse =
                GetStatementResponse.withFinalError(ResponseErrorCode.INVALID_ADAPTER,
                                                    "invalid adapter " + data.getCorebankAdapter());
            updateJobWithError(invalidAdapterResponse, data);
            return;
        }

        log.debug("adapter class: " + coreBank.getClass().getName());

        GetStatementResponse response = coreBank.getStatement(
            data.getAccount(),
            data.getType(),
            data.getCurrency(),
            data.getDateFrom(),
            data.getDateTo()
        );

        if (response.getHasError()) {
            updateJobWithError(response, data);
        } else {
            sendStatement(response, data);
            updateJobSuccess(response, data);
        }
    }

    private void sendStatement(final GetStatementResponse response, final JobTemplate data) {
        if (response.getStatement() != null) {
            log.debug(response.getStatement().toString());

            //send the statement to sender queue
            SenderTemplate senderTemplate = new SenderTemplate();
            senderTemplate.setStatement(response.getStatement());
            senderTemplate.setAccount(data.getAccount());
            senderTemplate.setAttemptNumber(data.getAttemptNumber());
            senderTemplate.setCurrency(data.getCurrency());
            senderTemplate.setCustomerCode(data.getCustomerCode());
            senderTemplate.setDateFrom(data.getDateFrom());
            senderTemplate.setFormatAdapter(data.getFormatAdapter());
            senderTemplate.setSendAdapter(data.getSendAdapter());
            senderTemplate.setDateTo(data.getDateTo());
            senderTemplate.setFrequency(data.getFrequency());
            senderTemplate.setId(data.getId());
            senderTemplate.setRecipient(data.getRecipient());

            statementProducer.send(data.getId().toString(), senderTemplate);
        }
    }

    private void updateJobWithError(final GetStatementResponse response, final JobTemplate data) {
        log.error(response.getErrorCode().toString() + " " + response.getErrorDesc());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode(response.getErrorCode().toString()); //todo: change in service-main
        updateJob.setErrorDesc(response.getErrorDesc());
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(response.getShouldRetryAgain());

        updateJobProducer.send(data.getId().toString(), updateJob);
    }

    private void updateJobSuccess(final GetStatementResponse response, final JobTemplate data) {
        log.info("success: " + response.getErrorDesc());

        UpdateJobTemplate updateJob = new UpdateJobTemplate();
        updateJob.setId(data.getId());
        updateJob.setAccount(data.getAccount());
        updateJob.setAdapterType("corebank");
        updateJob.setAdapterCode(data.getCorebankAdapter());
        updateJob.setErrorCode(response.getErrorCode().toString()); //todo: change in service-main
        updateJob.setErrorDesc(response.getErrorDesc());
        updateJob.setLocalDateTime(LocalDateTime.now());
        updateJob.setShouldTryAgain(response.getShouldRetryAgain());

        updateJobProducer.send(data.getId().toString(), updateJob);
    }

}
