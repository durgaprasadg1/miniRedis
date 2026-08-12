package com.miniredis.concurrency;

import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrencyExperimentTest {

    @Test
    void demonstrateRaceCondition() throws InterruptedException {

        int threadCount = 100;
        int incrementPerThread = 1000;

        int[] counter = { 0 };

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    int current = counter[0];
                    counter[0] = current + 1;
                }
            });

        }

        executor.shutdown();

        executor.awaitTermination(10, TimeUnit.SECONDS);
        int expected = threadCount * incrementPerThread;

        System.out.println(
                "Expected: " + expected);

        System.out.println(
                "Actual: " + counter[0]);
    }
}
