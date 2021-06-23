package com.valtech.teamb.kafka.kafkaworkshop;

import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingAccount;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingProducer;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingService;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingTransaction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class AdvancedTopicTopics {


    @Autowired
    private BankingProducer producer;

    @Autowired
    private KafkaStringMessageProducer shittyProducer;

    @Autowired
    private KafkaStringMessageConsumer consumer;

    // TODO: Replace Mock with your implementation/component
    @Mock
    private BankingService service;

    @Test
    void noiseToTheDeadLetterTopic() {
        var uniqueTestId = UUID.randomUUID().toString();
        var accountTopic = producer.getAccountTopic();
        var transactionTopic = producer.getTransactionTopic();
        var accountHolderTopic = producer.getAccountHolderTopic();

        shittyProducer.publish(accountTopic, "Dead-Letter" + uniqueTestId + ": " + producer.getAccountTopic());
        shittyProducer.publish(transactionTopic, "Dead-Letter" + uniqueTestId + ": " + producer.getTransactionTopic());
        shittyProducer.publish(accountHolderTopic, "Dead-Letter" + uniqueTestId + ": " + producer.getAccountHolderTopic());

        await().atMost(5, TimeUnit.SECONDS).until(() -> deadLetterMessagesReceived(uniqueTestId));
    }


    @Test
    void ignoreDuplicatedTransactionsWithSameContent() {
        var uuid = UUID.randomUUID();
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 1000);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(uuid, receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);


        producer.publish(senderAccount);
        producer.publish(receiverAccount);
        producer.publish(transaction);
        producer.publish(transaction);
        producer.publish(transaction);

        await().pollDelay(5, TimeUnit.SECONDS);

        assertThat(service.getAccountBalance(receiverAccount.getAccountNumber())).isEqualTo(100.0);
        assertThat(service.getAccountBalance(senderAccount.getAccountNumber())).isEqualTo(100.0);
        assertThat(deadLetterListDoesNotContain(uuid.toString())).isTrue();
    }

    @Test
    void publishDuplicatedTransactionsWithDifferentContent() {
        var uuid = UUID.randomUUID();
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 700);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(uuid, receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);
        var faultyId = new BankingTransaction(uuid, receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 200.0);


        producer.publish(senderAccount);
        producer.publish(receiverAccount);
        producer.publish(transaction);
        producer.publish(faultyId);

        await().pollDelay(5, TimeUnit.SECONDS);

        assertThat(service.getAccountBalance(receiverAccount.getAccountNumber())).isEqualTo(100.0);
        assertThat(service.getAccountBalance(senderAccount.getAccountNumber())).isEqualTo(600.0);
        assertThat(deadLetterListDoesContain(uuid.toString())).isTrue();
    }


    private boolean deadLetterMessagesReceived(String id) {
        var topics = consumer.getDeadLetters().stream()
                .filter(letter -> letter.getPayload().contains(id))
                .map(DeadLetterMessage::getSourceTopic)
                .collect(Collectors.toList());

        return topics.containsAll(List.of(producer.getAccountTopic(), producer.getAccountHolderTopic(), producer.getTransactionTopic()));
    }

    private boolean deadLetterListDoesNotContain(String key) {
        return consumer.getDeadLetters().stream()
                .noneMatch(letter -> letter.getPayload().contains(key));
    }

    private boolean deadLetterListDoesContain(String key) {
        return consumer.getDeadLetters().stream()
                .anyMatch(letter -> letter.getPayload().contains(key));
    }
}
