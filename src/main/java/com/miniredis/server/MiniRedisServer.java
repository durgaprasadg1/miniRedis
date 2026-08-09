package com.miniredis.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import com.miniredis.storage.RedisStorage;


public class MiniRedisServer {
    private final int port ;
    private final ExecutorService executor ;
    private final RedisStorage store ;

    public MiniRedisServer(int port, int threadCount){
        this.port = port;
        this.executor = Executors.newFixedThreadPool(threadCount);
        store = new RedisStorage();

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
                

                String[] parts = request.split(" ", 3);



                if(parts[0].equalsIgnoreCase("PING")){
                    writer.println("PONG");
                }else if(parts[0].equalsIgnoreCase("SET")){
                    int n = parts.length ;
                    if(n != 3){
                        writer.println("Err required 3 arguments received "+ n);
                        continue;
                    }

                    String key = parts[1];
                    String value = parts[2];
                    store.set(key, value);
                    writer.println("Ok");
                    
                }
                else if(parts[0].equalsIgnoreCase("GET")){
                    int n = parts.length ;

                    if(n != 2){
                        writer.println("Err required 2 arguments received "+ n);
                        continue;
                    }

                    String key = parts[1];
                   
                    String value = store.get(key);

                    if(value == null){
                        writer.println("(nil)");
                    }else{
                        writer.println(value);
                    }

                    writer.println("Ok");
                }
                else{

                    writer.println("ERR unknown command");
                }
            }
            
        }
        catch (IOException e) {
            System.out.println("Client Error "+ e.getMessage());
        }
        System.out.println("client Disconnected ");
    }
}
