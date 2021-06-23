package com.valtech.teamb.kafka.kafkaworkshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    private final List<DeadLetterMessage> deadLetters = new ArrayList<>();

    private static final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "${string.topic}")
    public void receive(ConsumerRecord<?, ?> consumerRecord) {
        LOGGER.info("received payload='{}'", consumerRecord.toString());
        payload = consumerRecord.toString();
    }

    @KafkaListener(topics = "${dead-letter.topic}")
    public void receiveDeadLetters(String message) {
        LOGGER.info("received dead letter message='{}'", message);
        try {
            deadLetters.add(mapper.readValue(message, DeadLetterMessage.class));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
