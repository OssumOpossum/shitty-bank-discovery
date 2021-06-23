package com.valtech.teamb.kafka.kafkaworkshop;

import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class KafkaStringMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStringMessageConsumer.class);

    private String payload = null;

    @KafkaListener(topics = "${string.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        payload = consumerRecord.toString();
    }
}
