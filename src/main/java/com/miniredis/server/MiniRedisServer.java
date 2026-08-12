package com.miniredis.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import com.miniredis.storage.RedisStorage;
import com.miniredis.command.Command;
import com.miniredis.command.CommandDispatcher;
import com.miniredis.command.CommandParser;
import com.miniredis.protocol.Encoder;
import com.miniredis.protocol.Decoder;

public class MiniRedisServer {
    private final int port;
    private final ExecutorService executor;
    private final RedisStorage store;
    private final CommandParser parser;
    private final CommandDispatcher dispatcher;

    private ServerSocket serverSocket;

    public MiniRedisServer(int port, int threadCount) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.store = new RedisStorage();
        this.parser = new CommandParser();
        this.dispatcher = new CommandDispatcher(store);
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Mini Redis server started on  : " + port);

        while (!serverSocket.isClosed()) {

            Socket clientSocket = serverSocket.accept();

            System.out.println(
                    "Client Connected "
                            + clientSocket.getRemoteSocketAddress());

            executor.submit(
                    () -> handleClient(clientSocket));
        }
    }

    public void stop() throws IOException {

        if (serverSocket != null &&
                !serverSocket.isClosed()) {

            serverSocket.close();
        }

        executor.shutdownNow();

        System.out.println(
                "Mini Redis server stopped");
    }

    private void handleClient(Socket socket) {
        try (socket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true) // an outoutStream , autoFlush

        ) {

            CommandParser parser = new CommandParser();
            Decoder decoder = new Decoder(parser);
            Encoder encoder = new Encoder();

            CommandDispatcher dispatcher = new CommandDispatcher(store);

            String request;
            while ((request = reader.readLine()) != null) {
                System.out.println("Request " + request);

                Command command = decoder.decode(request);

                String response = dispatcher.execute(command);

                writer.print(
                        encoder.encode(response));

                writer.flush();
            }

        } catch (IOException e) {
            System.out.println("Client Error " + e.getMessage());
        }
        System.out.println("client Disconnected ");
    }
}
