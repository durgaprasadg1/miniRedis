package com.miniredis.concurrency;

import com.miniredis.storage.RedisStorage;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisStorageConcurrencyTest {

    @Test
    void concurrentIncrementShouldBeCorrect()
            throws InterruptedException {

        RedisStorage storage = new RedisStorage();

        storage.set("counter", "0");

        int threadCount = 100;
        int incrementsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {

            executor.submit(() -> {

                for (int j = 0; j < incrementsPerThread; j++) {

                    storage.increment("counter");
                }
            });
        }

        executor.shutdown();

        executor.awaitTermination(
                10,
                TimeUnit.SECONDS);

        int expected = threadCount * incrementsPerThread;

        int actual = Integer.parseInt(
                storage.get("counter"));

        System.out.println("Expected: " + expected);
        System.out.println("Actual: " + actual);

        assertEquals(expected, actual);
    }
}