package com.valtech.teamb.kafka.kafkaworkshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingAccount;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingAccountOwner;
import com.valtech.teamb.kafka.kafkaworkshop.banking.BankingTransaction;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testTransactionSerialization() throws JsonProcessingException {
        var transaction = new BankingTransaction(UUID.randomUUID(), "123", "321", 100.0);
        var deserializedTransaction = mapper.readValue(mapper.writeValueAsString(transaction), BankingTransaction.class);

        assertThat(deserializedTransaction).isEqualTo(transaction);
    }

    @Test
    void testAccountSerialization() throws JsonProcessingException {
        var account = new BankingAccount("123", 123.0, 100.0);
        var deserializedAccount = mapper.readValue(mapper.writeValueAsString(account), BankingAccount.class);

        assertThat(deserializedAccount).isEqualTo(account);
    }

    @Test
    void testAccountHolderSerialization() throws JsonProcessingException {
        var accountHolder = new BankingAccountOwner("123", "Harald Schmidt");
        var deserializedAccountHolder = mapper.readValue(mapper.writeValueAsString(accountHolder), BankingAccountOwner.class);

        assertThat(deserializedAccountHolder).isEqualTo(accountHolder);
    }
}
