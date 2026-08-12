package com.miniredis.integration;

import com.miniredis.server.MiniRedisServer;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MiniRedisIntegrationTest {

    @Test
    void shouldRespondToPing() throws Exception {

        MiniRedisServer server = new MiniRedisServer(6380, 4);

        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                // Server stopped during test
            }
        });

        serverThread.start();

        Thread.sleep(200);

        try (
                Socket socket = new Socket("localhost", 6380);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));

                PrintWriter writer = new PrintWriter(
                        socket.getOutputStream(),
                        true)) {

            writer.println("PING");

            String response = reader.readLine();

            assertEquals("PONG", response);

        } finally {

            server.stop();

            serverThread.join(1000);
        }
    }
}