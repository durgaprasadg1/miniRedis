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

    @Test
    void shouldIncrementValue() {

        RedisStorage storage = new RedisStorage();

        storage.set("counter", "10");

        assertEquals(11, storage.increment("counter"));
        assertEquals("11", storage.get("counter"));
    }

    @Test
    void shouldIncrementMultipleTimes() {

        RedisStorage storage = new RedisStorage();

        storage.set("counter", "0");

        storage.increment("counter");
        storage.increment("counter");
        storage.increment("counter");

        assertEquals("3", storage.get("counter"));
    }

    @Test
    void changeShouldIncrement() {

        RedisStorage storage = new RedisStorage();

        assertEquals(1, storage.change("counter", 1));

        assertEquals(6, storage.change("counter", 5));
    }

    @Test
    void changeShouldDecrement() {

        RedisStorage storage = new RedisStorage();
        assertEquals(-1, storage.change("counter", -1));
        assertEquals(-6, storage.change("counter", -5));
    }

    @Test
    void changeShouldRejectNonInteger() {

        RedisStorage storage = new RedisStorage();
        storage.set("counter", "hello");
        assertThrows(NumberFormatException.class, () -> storage.change("counter", 1));
    }

    @Test
    void expireShouldReturnZeroForMissingKey() {

        RedisStorage storage = new RedisStorage();

        assertEquals(
                0,
                storage.expire("name", 10));
    }

    @Test
    void ttlShouldReturnMinusOneWithoutExpiration() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        assertEquals(
                -1,
                storage.ttl("name"));
    }

    @Test
    void ttlShouldReturnMinusTwoForMissingKey() {

        RedisStorage storage = new RedisStorage();

        assertEquals(
                -2,
                storage.ttl("name"));
    }

    @Test
    void persistShouldRemoveExpiration() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        storage.expire("name", 10);

        assertEquals(
                1,
                storage.persist("name"));

        assertEquals(
                -1,
                storage.ttl("name"));
    }

    @Test
    void expiredKeyShouldBehaveAsMissing() throws InterruptedException {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        storage.expire("name", 1);

        Thread.sleep(1100);

        assertNull(
                storage.get("name"));

        assertEquals(
                0,
                storage.exists("name"));
    }
}