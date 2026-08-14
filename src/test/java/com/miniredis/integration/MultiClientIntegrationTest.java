package com.miniredis.integration;

import com.miniredis.server.MiniRedisServer;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiClientIntegrationTest {

    @Test
    void shouldHandleMultipleClients()
            throws Exception {

        MiniRedisServer server = new MiniRedisServer(6380, 4);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();

        server.awaitStartup();

        int clientCount = 10;

        ExecutorService clients = Executors.newFixedThreadPool(clientCount);

        List<Future<String>> results = new ArrayList<>();

        try {

            for (int i = 0; i < clientCount; i++) {

                final int clientId = i;

                results.add(
                        clients.submit(() -> runClient(clientId)));
            }

            for (int i = 0; i < clientCount; i++) {

                assertEquals(
                        "value-" + i,
                        results.get(i).get());
            }

        } finally {

            clients.shutdownNow();

            server.stop();

            serverThread.join();
        }
    }

    private String runClient(int clientId)
            throws IOException {

        String key = "client-" + clientId;

        String value = "value-" + clientId;

        try (
                Socket socket = new Socket("localhost", 6380);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true)) {

            writer.println(
                    "SET " + key + " " + value);

            assertEquals(
                    "OK",
                    reader.readLine());

            writer.println(
                    "GET " + key);

            return reader.readLine();
        }
    }
}