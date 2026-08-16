package com.miniredis.storage;

// import Entry
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisStorage {

    private final Map<String, Entry> data;

    public RedisStorage() {
        data = new ConcurrentHashMap<>();
    }

    public void set(String key, String value) {
        data.put(key, new Entry(DataType.STRING, value));
    }

    public String get(String key) {

        Entry value = getLiveEntry(key);
        if (value == null)
            return null;
        return (String) value.getValue();
    }

    public int delete(String key) {
        Entry entry = getLiveEntry(key);
        if (entry == null)
            return 0;
        return data.remove(key) != null ? 1 : 0;
    }

    public int exists(String key) {
        return getLiveEntry(key) != null ? 1 : 0;
    }

    public int size() {
        int count = 0;
        for (String key : data.keySet()) {
            if (getLiveEntry(key) != null) {
                count++;
            }
        }
        return count;
    }

    public int increment(String key) {
        return change(key, 1);
    }

    public synchronized int change(String key, int delta) {

        Entry entry = data.get(key);
        int number;
        if (entry == null) {
            number = 0;
            entry = new Entry(DataType.STRING, "0");
            data.put(key, entry);
        } else {
            if (entry.getType() != DataType.STRING) {
                throw new IllegalStateException("WrongType");
            }

            number = Integer.parseInt((String) entry.getValue());
        }
        number += delta;
        entry.setValue(String.valueOf(number));
        return number;
    }

    private Entry getLiveEntry(String key) {

        Entry entry = data.get(key);
        if (entry == null)
            return null;

        if (entry.getExpiresAt() == -1) {
            return entry;
        }
        if (System.currentTimeMillis() >= entry.getExpiresAt()) {
            data.remove(key);
            return null;
        }
        return entry;
    }

    public int expire(String key, long seconds) {
        Entry entry = getLiveEntry(key);
        if (entry == null) {
            return 0;
        }
        long expiresAt = System.currentTimeMillis() + (seconds * 1000);
        entry.setExpiresAt(expiresAt);
        return 1;

    }

    public long ttl(String key) {
        Entry entry = getLiveEntry(key);
        if (entry == null)
            return -2;

        if (entry.getExpiresAt() == -1)
            return -1;
        long remaining = entry.getExpiresAt() - System.currentTimeMillis();
        return Math.max(0, (remaining + 999) / 1000);
    }

    public int persist(String key) {
        Entry entry = getLiveEntry(key);
        if (entry == null)
            return 0;

        if (entry.getExpiresAt() == -1)
            return 0;

        entry.setExpiresAt(-1);
        return 1;
    }

}
