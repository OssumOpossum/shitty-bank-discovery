package com.valtech.teamb.kafka.kafkaworkshop;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KafkaStringMessageProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStringMessageProducer.class);

    private final KafkaTemplate<Integer, String> kafkaTemplate;

    public void publish(String topic, String payload) {
        LOGGER.info("Sending payload='{}' to topic='{}'", payload, topic);
        kafkaTemplate.send(topic, payload);
    }
}
