package com.valtech.teamb.kafka.kafkaworkshop.banking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class BankingAccount implements Banking {

    private final String accountNumber;

    private final double creditLine;

    private final double balance;

    @JsonCreator
    public static BankingAccount fromJson(
            @JsonProperty("accountNumber") String accountNumber,
            @JsonProperty("creditLine") double creditLine,
            @JsonProperty("balance") double balance
    ) {
        return new BankingAccount(accountNumber, creditLine, balance);
    }
}
