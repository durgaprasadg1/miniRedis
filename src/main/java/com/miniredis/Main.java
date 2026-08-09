package com.miniredis;
import com.miniredis.server.MiniRedisServer;
public class Main {
    
    public static void main(String[] args) throws Exception {
        MiniRedisServer server = new MiniRedisServer(6379, 4);

        server.start();     
    }
}
