package com.valtech.teamb.kafka.kafkaworkshop.banking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class BankingProducer {

    @Value("${banking.transaction.topic}")
    private String transactionTopic;

    @Value("${banking.account.topic}")
    private String accountTopic;

    @Value("${banking.account-holder.topic}")
    private String accountHolderTopic;

    private final KafkaTemplate<Integer, Banking> kafkaTemplate;

    public void publish(BankingTransaction transaction) {
        log.info("Publishing transaction='{}' to topic ='{}'", transaction, transactionTopic);
        kafkaTemplate.send(transactionTopic, transaction);
    }

    public void publish(BankingAccount account) {
        log.info("Publishing account='{}' to topic ='{}'", account, accountTopic);
        kafkaTemplate.send(accountTopic, account);
    }

    public void publish(BankingAccountOwner holder) {
        log.info("Publishing account holder='{}' to topic ='{}'", holder, accountHolderTopic);
        kafkaTemplate.send(accountHolderTopic, holder);
    }

    public void publish(BankingAccount... accounts) {
        for (BankingAccount account : accounts) {
            publish(account);
        }
    }

    public void publish(BankingTransaction... transactions) {
        for (BankingTransaction transaction : transactions) {
            publish(transaction);
        }
    }
}
