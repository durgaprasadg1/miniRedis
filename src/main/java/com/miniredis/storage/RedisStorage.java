package com.miniredis.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {

    private final Map<String, String> data;

    public RedisStorage() {
        data = new ConcurrentHashMap<>();
    }

    public void set(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public int delete(String key) {
        return data.remove(key) != null ? 1 : 0;
    }

    public int exists(String key) {
        return data.containsKey(key) ? 1 : 0;
    }

    public int size() {
        return data.size();
    }

    public int increment(String key) {
        return change(key, 1);
    }

    public synchronized int change(String key, int delta) {

        String value = get(key);

        int number;

        if (value == null) {
            number = 0;
        } else {
            number = Integer.parseInt(value);
        }

        number += delta;

        set(key, String.valueOf(number));

        return number;
    }
}
