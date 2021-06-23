package com.valtech.teamb.kafka.kafkaworkshop;

import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class AdvancedTopics {


    @Autowired
    private BankingProducer producer;

    @Autowired
    private KafkaStringMessageProducer shittyProducer;

    @Autowired
    private KafkaStringMessageConsumer consumer;


    @Test
    void weNeedADeadLetterTopic() {
        var uniqueTestId = UUID.randomUUID().toString();

        shittyProducer.publish(producer.getAccountTopic(), "Dead-Letter" + uniqueTestId + ": " + producer.getAccountTopic() );
        shittyProducer.publish(producer.getTransactionTopic(), "Dead-Letter" + uniqueTestId + ": " + producer.getTransactionTopic());
        shittyProducer.publish(producer.getAccountHolderTopic(), "Dead-Letter" + uniqueTestId + ": " + producer.getAccountHolderTopic());

        await().atMost(5, TimeUnit.SECONDS).until(() -> deadLetterMessagesReceived(uniqueTestId));
    }


    private boolean deadLetterMessagesReceived(String id) {
        var topics = consumer.getDeadLetters().stream()
                .filter(letter -> letter.toString().contains(id))
                .map(ConsumerRecord::topic)
                .collect(Collectors.toList());

        return topics.containsAll(List.of(producer.getAccountTopic(), producer.getAccountHolderTopic(), producer.getTransactionTopic()));
    }
}
