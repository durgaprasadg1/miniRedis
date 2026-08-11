package com.miniredis.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import com.miniredis.storage.RedisStorage;
import com.miniredis.command.Command;
import com.miniredis.command.CommandDispatcher;
import com.miniredis.command.CommandParser;


public class MiniRedisServer {
    private final int port ;
    private final ExecutorService executor ;
    private final RedisStorage store ;
    private final CommandParser parser;
    private final CommandDispatcher dispatcher;

    public MiniRedisServer(int port, int threadCount) {
        this.port = port;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.store = new RedisStorage();
        this.parser = new CommandParser();
        this.dispatcher = new CommandDispatcher(store);
    }


    public void start() throws IOException{

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Mini Redis server started on  : "+ port);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            System.out.println("Client Connected "+ clientSocket.getRemoteSocketAddress());


            executor.submit(()-> handleClient(clientSocket));
        }
    }

    private void handleClient(Socket socket){
        try(socket;
            BufferedReader reader =  new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true) // an outoutStream , autoFlush 

        ){
            String request ;
            while((request = reader.readLine()) != null){
                System.out.println("Request "+ request);        
                
               Command command = parser.parse(request);

                String response =  dispatcher.execute(command);
                writer.println(response);
            }
            
        }
        catch (IOException e) {
            System.out.println("Client Error "+ e.getMessage());
        }
        System.out.println("client Disconnected ");
    }
}

