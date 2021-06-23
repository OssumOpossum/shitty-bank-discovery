package com.valtech.teamb.kafka.kafkaworkshop.banking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class BankingTransaction implements Banking {

    private final UUID transactionId;

    private final String receivingAccount;

    private final String sendingAccount;

    private final double amount;


    @JsonCreator
    public static BankingTransaction fromJson(
            @JsonProperty("transactionId") UUID transactionId,
            @JsonProperty("receivingAccount") String receivingAccount,
            @JsonProperty("sendingAccount") String sendingAccount,
            @JsonProperty("amount") double amount
    ) {
        return new BankingTransaction(transactionId, receivingAccount, sendingAccount, amount);
    }
}
