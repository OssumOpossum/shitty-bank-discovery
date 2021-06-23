package com.valtech.teamb.kafka.kafkaworkshop;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Getter
public class KafkaStringMessageConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStringMessageConsumer.class);

    private String payload = null;

    @KafkaListener(topics = "${string.topic}")
    public void receive(String receivedPayload) {
        LOGGER.info("received payload='{}'", payload);
        payload = receivedPayload;
    }
}
