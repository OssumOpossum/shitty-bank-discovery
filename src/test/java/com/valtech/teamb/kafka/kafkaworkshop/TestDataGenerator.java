package com.valtech.teamb.kafka.kafkaworkshop;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TestDataGenerator {

    private static final AtomicInteger numberGenerator = new AtomicInteger(0);
    private static final String[] names = new String[]{
            "Ben",
            "Paul",
            "Jonas",
            "Elias",
            "Leon",
            "Mia",
            "Emma",
            "Sofia",
            "Hannah",
            "Emilia",
            "Anna",
            "Marie",
            "Mila"
    };

    public static final Supplier<String> ACCOUNT_HOLDER_GENERATOR = () -> {
        int rnd = new Random().nextInt(names.length);
        return names[rnd] + numberGenerator.incrementAndGet();
    };

    public static final Supplier<String> BANK_ACCOUNT_GENERATOR = () -> "900000" + numberGenerator.incrementAndGet();
}
