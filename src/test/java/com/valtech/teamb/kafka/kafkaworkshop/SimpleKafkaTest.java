package com.valtech.teamb.kafka.kafkaworkshop;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class SimpleKafkaTest {

    @Autowired
    private KafkaStringMessageConsumer consumer;

    @Autowired
    private KafkaStringMessageProducer producer;

    @Value("${string.topic}")
    private String topic;

    @Test
    public void simpleMessageReceived() {
        producer.publish(topic, "Some test message");
        await().atMost(5, SECONDS).until(() -> consumer.getPayload() != null);

        assertThat(consumer.getPayload()).contains("Some test message");
    }

}
