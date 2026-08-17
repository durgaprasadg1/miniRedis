package com.miniredis.storage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
// import Entry
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisStorage {

    private final Map<String, Entry> data;

    private final ScheduledExecutorService expirationExecutor;

    public RedisStorage() {
        data = new ConcurrentHashMap<>();
        expirationExecutor = Executors.newSingleThreadScheduledExecutor();
        expirationExecutor.scheduleAtFixedRate(this::removeExpiredEntries, 1, 1, TimeUnit.SECONDS); // Hrr ek second me
                                                                                                    // chalega like run
                                                                                                    // -> wait for a sec
                                                                                                    // -> run
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

    private void removeExpiredEntries() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Entry> entry : data.entrySet()) {
            Entry value = entry.getValue();

            long expireAt = value.getExpiresAt();
            if (expireAt != -1 && now >= expireAt) {
                data.remove(entry.getKey(), value); // yaha dono diye hai, iska mtlb exact mapping match honi chahiye
                                                    // naa ki sirf key.
            }

        }
    }

    public synchronized int pushLeft(String key, String value) {
        Entry entry = getLiveEntry(key);
        Deque<String> list;
        if (entry == null) {
            list = new ArrayDeque<>();
            entry = new Entry(DataType.LIST, list);
            data.put(key, entry);

        } else {

            if (entry.getType() != DataType.LIST) {
                throw new IllegalStateException("WrongType");
            }

            list = (Deque<String>) entry.getValue();
        }
        list.addFirst(value);
        return list.size();
    }

    public synchronized int pushRight(String key, String value) {

        Entry entry = getLiveEntry(key);

        Deque<String> list;

        if (entry == null) {

            list = new ArrayDeque<>();

            entry = new Entry(
                    DataType.LIST,
                    list);

            data.put(key, entry);

        } else {

            if (entry.getType() != DataType.LIST) {
                throw new IllegalStateException("WrongType");
            }

            list = (Deque<String>) entry.getValue();
        }

        list.addLast(value);

        return list.size();
    }

    public void shutDown() {

        expirationExecutor.shutdownNow();
    }

    List<String> getList(String key) {

        Entry entry = getLiveEntry(key);

        if (entry == null) {
            return null;
        }

        if (entry.getType() != DataType.LIST) {
            throw new IllegalStateException("WrongType");
        }

        return new ArrayList<>(
                (Deque<String>) entry.getValue());
    }

    int rawSize() {
        return data.size();
    }
}
