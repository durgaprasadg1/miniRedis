package com.miniredis.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedisStorageTest {

    @Test
    void shouldStoreAndRetrieveValue() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        String value = storage.get("name");

        assertEquals("durga", value);
    }

    @Test
    void shouldOverwriteExistingValue() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "abc");
        storage.set("name", "xyz");

        assertEquals("xyz", storage.get("name"));
    }

    @Test
    void shouldReturnNullForMissingKey() {

        RedisStorage storage = new RedisStorage();

        assertNull(storage.get("missing"));
    }

    @Test
    void shouldDeleteExistingKey() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        assertEquals(1, storage.delete("name"));
        assertNull(storage.get("name"));
    }

    @Test
    void shouldReturnZeroWhenDeletingMissingKey() {

        RedisStorage storage = new RedisStorage();

        assertEquals(0, storage.delete("missing"));
    }

    @Test
    void shouldReturnOneForExistingKey() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        assertEquals(1, storage.exists("name"));
    }

    @Test
    void shouldReturnZeroForMissingKey() {

        RedisStorage storage = new RedisStorage();

        assertEquals(0, storage.exists("missing"));
    }

    @Test
    void shouldReturnNumberOfKeys() {

        RedisStorage storage = new RedisStorage();

        storage.set("a", "10");
        storage.set("b", "20");
        storage.set("c", "30");

        assertEquals(3, storage.size());
    }

    @Test
    void shouldDecreaseSizeAfterDelete() {

        RedisStorage storage = new RedisStorage();

        storage.set("a", "10");
        storage.set("b", "20");

        storage.delete("a");

        assertEquals(1, storage.size());
    }
}