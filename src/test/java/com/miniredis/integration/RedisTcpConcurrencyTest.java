package com.miniredis.integration;

import com.miniredis.server.MiniRedisServer;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisTcpConcurrencyTest {

    @Test
    void concurrentTcpIncrementsShouldBeCorrect()
            throws Exception {

        int clientCount = 100;

        MiniRedisServer server =
                new MiniRedisServer(6380, 4);

        Thread serverThread =
                new Thread(() -> {
                    try {
                        server.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        serverThread.start();

        server.awaitStartup();

        ExecutorService clients =
                Executors.newFixedThreadPool(20);

        List<Future<Integer>> results =
                new ArrayList<>();

        try {

            for (int i = 0; i < clientCount; i++) {

                results.add(
                        clients.submit(
                                this::incrementCounter
                        )
                );
            }

            for (Future<Integer> result : results) {
                result.get();
            }

            int finalValue =
                    getCounter();

            assertEquals(
                    clientCount,
                    finalValue
            );

        } finally {

            clients.shutdownNow();

            server.stop();

            serverThread.join();
        }
    }

    private int incrementCounter()
            throws IOException {

        try (
                Socket socket =
                        new Socket("localhost", 6380);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter writer =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        )
        ) {

            writer.println("INCR counter");

            String response =
                    reader.readLine();

            return Integer.parseInt(response);
        }
    }

    private int getCounter()
            throws IOException {

        try (
                Socket socket =
                        new Socket("localhost", 6380);

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream()
                                )
                        );

                PrintWriter writer =
                        new PrintWriter(
                                socket.getOutputStream(),
                                true
                        )
        ) {

            writer.println("GET counter");

            return Integer.parseInt(
                    reader.readLine()
            );
        }
    }
}
