package com.valtech.teamb.kafka.kafkaworkshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class KafkaWorkshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaWorkshopApplication.class, args);
    }

}
