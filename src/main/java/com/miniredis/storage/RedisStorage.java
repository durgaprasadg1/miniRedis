package com.miniredis.storage;


import java.util.HashMap;   
import java.util.Map;
public class RedisStorage {

    private final Map<String,String > data ;

    public RedisStorage(){
        data = new HashMap<>();
    }

    public void set(String key, String value){
        data.put(key, value);
    }
    public String get(String key){
        
            return data.get(key);
        
        // return "No key exists";
    }
    public int size(){
        return data.size();
    }
    
}
