package cc.viridian.service.statement.repository;

import cc.viridian.provider.CoreBankProvider;
import cc.viridian.provider.model.Statement;
import cc.viridian.service.statement.payload.JobTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@Slf4j
public class StatementJobListener {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    StatementJobProducer statementJobProducer;

    @KafkaListener(topics = "${topic.statement.jobs}")
    public void receive(@Payload JobTemplate data,
                        @Headers MessageHeaders headers) {
        log.info("received data");

        log.info(data.getAccount());
        log.info(data.getCoreBankAdapter());
        log.info(data.getCurrency());
        log.info(data.getCustomerCode());
        log.info(data.getFormatAdapter());
        log.info(data.getFrequency());
        log.info(data.getRecipient());
        log.info(data.getSendAdapter());
        log.info(data.getType());

        log.info("key:" + headers.get("kafka_receivedMessageKey"));
        log.info("partition:" + headers.get("kafka_receivedPartitionId"));
        log.info("topic:" + headers.get("kafka_receivedTopic"));
        log.info("offset:" + headers.get("kafka_offset"));


        /*
        CoreBankProvider coreBank = CoreBankProvider.getInstance();
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now().minusDays(1);
        Statement statement = coreBank.getStatement(data.getAccount(), data.getType(), data.getCurrency(), from, to );

        System.out.println( statement);
        System.out.println( statement.getHeader());
        System.out.println( statement.getDetails());
        */

        String keyMessage = headers.get("kafka_receivedMessageKey").toString();
        JobTemplate response = new JobTemplate();
        response.setAccount( data.getAccount());
        response.setFormatAdapter( data.getFormatAdapter());
        response.setSendAdapter(data.getSendAdapter());

        statementJobProducer.send(keyMessage, response);
    }
}
