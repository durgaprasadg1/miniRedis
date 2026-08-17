package com.miniredis.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.miniredis.command.Command;
import com.miniredis.command.CommandDispatcher;
import com.miniredis.command.CommandParser;
import com.miniredis.protocol.Decoder;
import com.miniredis.protocol.Encoder;
import com.miniredis.storage.RedisStorage;

public class MiniRedisServer {

    private final int port;
    private final ExecutorService executor;
    private final RedisStorage store;

    private final CommandParser parser;
    private final CommandDispatcher dispatcher;

    private final Decoder decoder;
    private final Encoder encoder;

    private final CountDownLatch startupLatch = new CountDownLatch(1);

    private ServerSocket serverSocket;

    public MiniRedisServer(int port, int threadCount) {

        this.port = port;

        this.executor = Executors.newFixedThreadPool(threadCount);

        this.store = new RedisStorage();

        this.parser = new CommandParser();

        this.dispatcher = new CommandDispatcher(store);

        this.decoder = new Decoder(parser);

        this.encoder = new Encoder();
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        startupLatch.countDown();
        System.out.println("Mini Redis server started on : " + port);

        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Connected " + clientSocket.getRemoteSocketAddress());
                executor.submit(() -> handleClient(clientSocket));

            } catch (IOException e) {

                if (serverSocket.isClosed()) {
                    break;
                }

                throw e;
            }
        }
    }

    public void awaitStartup()
            throws InterruptedException {

        startupLatch.await();
    }

    public void stop() throws IOException {

        if (serverSocket != null &&
                !serverSocket.isClosed()) {

            serverSocket.close();
        }

        executor.shutdownNow();
        store.shutDown();
        

        System.out.println(
                "Mini Redis server stopped");
    }

    private void handleClient(Socket socket) {

        try (
                socket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String request;

            Command command;

            while ((command = decoder.decode(reader)) != null) {

                System.out.println(
                        "Request " + command.getName());

                String response = dispatcher.execute(command);

                writer.print(
                        encoder.encode(response));

                writer.flush();
            }

        } catch (IOException e) {

            System.out.println(
                    "Client Error " + e.getMessage());
        }

        System.out.println(
                "Client Disconnected");
    }

}