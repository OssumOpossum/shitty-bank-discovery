package com.valtech.teamb.kafka.kafkaworkshop;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DeadLetterMessage {

    private final String sourceTopic;

    private final String payload;

    @JsonCreator
    public static DeadLetterMessage fromJson(
            @JsonProperty("sourceTopic") String sourceTopic,
            @JsonProperty("payload") String payload
    ) {
        return new DeadLetterMessage(sourceTopic, payload);
    }
}
