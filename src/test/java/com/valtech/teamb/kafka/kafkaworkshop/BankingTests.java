package com.valtech.teamb.kafka.kafkaworkshop;


import com.valtech.teamb.kafka.kafkaworkshop.banking.*;
import com.valtech.teamb.kafka.kafkaworkshop.banking.exception.UnknownAccountException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class BankingTests {

    @Autowired
    private BankingProducer producer;

    @Autowired
    private KafkaStringMessageProducer shittyProducer;


    // TODO: Replace Mock with your implementation/component
    @Mock
    private BankingService service;

    @Test
    public void basicValidTransaction() {
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 500);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(UUID.randomUUID(), receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);

        producer.publish(transaction);
        producer.publish(senderAccount);
        producer.publish(receiverAccount);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(transaction.getTransactionId()));

        assertThat(service.isTransactionValid(transaction.getTransactionId())).isTrue();
    }

    @Test
    public void balanceCalculation() {
        var firstAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 1000);
        var secondAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var thirdAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var firstTransaction = new BankingTransaction(UUID.randomUUID(), secondAccount.getAccountNumber(), firstAccount.getAccountNumber(), 250.0);
        var secondTransaction = new BankingTransaction(UUID.randomUUID(), thirdAccount.getAccountNumber(), firstAccount.getAccountNumber(), 150.0);
        var thirdTransaction = new BankingTransaction(UUID.randomUUID(), secondAccount.getAccountNumber(), thirdAccount.getAccountNumber(), 50.0);
        var fourthTransaction = new BankingTransaction(UUID.randomUUID(), secondAccount.getAccountNumber(), thirdAccount.getAccountNumber(), 500.0);

        producer.publish(firstAccount, secondAccount, thirdAccount);
        producer.publish(firstTransaction, secondTransaction, thirdTransaction, fourthTransaction);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(fourthTransaction.getTransactionId()));

        assertThat(service.getAccountBalance(firstAccount.getAccountNumber())).isEqualTo(600);
        assertThat(service.getAccountBalance(secondAccount.getAccountNumber())).isEqualTo(300);
        assertThat(service.getAccountBalance(thirdAccount.getAccountNumber())).isEqualTo(100);
        assertThat(service.isTransactionValid(fourthTransaction.getTransactionId())).isFalse();
    }


    @Test
    public void basicInvalidTransaction() {
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(UUID.randomUUID(), receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);

        producer.publish(senderAccount);
        producer.publish(receiverAccount);
        producer.publish(transaction);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(transaction.getTransactionId()));

        assertThat(service.isTransactionValid(transaction.getTransactionId())).isFalse();
    }


    @Test
    public void thePoisonedPill() {
        var uuid = UUID.randomUUID();
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(uuid, receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);

        shittyProducer.publish(producer.getAccountTopic(), "Oops - something went horribly wrong.");
        producer.publish(senderAccount);
        producer.publish(receiverAccount);
        producer.publish(transaction);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(uuid));

        assertThat(service.isTransactionValid(uuid)).isFalse();
    }


    @Test
    public void eventualConsistencyIsConsistent_Eventually_Maybe() {
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 1000.0, 0);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(UUID.randomUUID(), receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);

        producer.publish(transaction);

        await().pollDelay(2, SECONDS).until(() -> true);

        producer.publish(senderAccount);
        producer.publish(receiverAccount);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(transaction.getTransactionId()));

        assertThat(service.isTransactionValid(transaction.getTransactionId())).isTrue();
    }

    @Test
    public void eventualConsistencyIsConsistent_Eventually_Maybe_Not() {
        var senderAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 1000.0, 0);
        var receiverAccount = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var transaction = new BankingTransaction(UUID.randomUUID(), receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), 100.0);

        producer.publish(transaction);

        await().pollDelay(10, SECONDS).until(() -> true);

        producer.publish(senderAccount);
        producer.publish(receiverAccount);

        await().atMost(5, SECONDS).until(() -> service.isTransactionKnown(transaction.getTransactionId()));

        assertThat(service.isTransactionValid(transaction.getTransactionId())).isFalse();
    }

    @Test
    public void unknownAccountThrowsEggception() {
        var account = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);

        assertThatThrownBy(() -> service.getAccountBalance(account.getAccountNumber())).isInstanceOf(UnknownAccountException.class);

        producer.publish(account);

        await().atMost(5, SECONDS)
                .ignoreExceptions()
                .until(() -> service.getAccountBalance(account.getAccountNumber()) == 0.0);
    }


    @Test
    public void accountOwnerIsOptional() {
        var account = new BankingAccount(TestDataGenerator.BANK_ACCOUNT_GENERATOR.get(), 0, 0);
        var owner = new BankingAccountOwner(account.getAccountNumber(), "Josef Stalin");

        producer.publish(account);

        await().atMost(5, SECONDS)
                .ignoreExceptions()
                .until(() -> service.getAccountBalance(account.getAccountNumber()) == 0.0);

        assertThat(service.getHolderForAccountNumber(account.getAccountNumber())).isEmpty();

        producer.publish(owner);

        await().atMost(5, SECONDS)
                .untilAsserted(() -> service.getHolderForAccountNumber(owner.getAccountNumber()).isPresent());

        assertThat(service.getHolderForAccountNumber(owner.getAccountNumber()).orElseThrow()).isEqualTo(owner);
    }

}

