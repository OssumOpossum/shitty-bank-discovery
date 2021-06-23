package com.valtech.teamb.kafka.kafkaworkshop.banking;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@RequiredArgsConstructor
@ToString
@Getter
@EqualsAndHashCode
public final class BankingAccountOwner implements Banking {

    private final String accountNumber;

    private final String accountHolder;

    @JsonCreator
    public static BankingAccountOwner fromJson(
            @JsonProperty("accountNumber") String accountNumber,
            @JsonProperty("accountHolder") String accountHolder
    ) {
        return new BankingAccountOwner(accountNumber, accountHolder);
    }

}
