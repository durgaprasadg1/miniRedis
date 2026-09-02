package com.miniredis.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

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

    @Test
    void activeExpirationShouldRemoveExpiredEntries()
            throws InterruptedException {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        storage.expire("name", 1);

        assertEquals(1, storage.rawSize());

        Thread.sleep(2500);

        assertEquals(0, storage.rawSize());

        storage.shutDown();
    }

    @Test
    void shouldPushLeft() {

        RedisStorage storage = new RedisStorage();

        try {

            assertEquals(
                    1,
                    storage.pushLeft("queue", "A"));

            assertEquals(
                    2,
                    storage.pushLeft("queue", "B"));

            assertEquals(
                    3,
                    storage.pushLeft("queue", "C"));

        } finally {

            storage.shutDown();
        }
    }

    @Test
    void shouldPushLeftInCorrectOrder() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "A");
            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "C");

            assertEquals(
                    List.of("C", "B", "A"),
                    storage.getList("queue"));

        } finally {

            storage.shutDown();
        }
    }

    @Test
    void shouldRejectPushLeftOnString() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.set("name", "durga");

            assertThrows(
                    IllegalStateException.class,
                    () -> storage.pushLeft(
                            "name",
                            "hello"));

        } finally {

            storage.shutDown();
        }
    }

    @Test
    void shouldPushRight() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushRight("queue", "A");
            storage.pushRight("queue", "B");
            storage.pushRight("queue", "C");

            assertEquals(
                    List.of("A", "B", "C"),
                    storage.getList("queue"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldPushLeftAndRight() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "A");
            storage.pushRight("queue", "C");

            assertEquals(
                    List.of("A", "B", "C"),
                    storage.getList("queue"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldPopLeft() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "A");
            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "C");

            assertEquals(
                    "C",
                    storage.popLeft("queue"));

            assertEquals(
                    2,
                    storage.listLength("queue"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldPopRight() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "A");
            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "C");

            assertEquals(
                    "A",
                    storage.popRight("queue"));

            assertEquals(
                    2,
                    storage.listLength("queue"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldReturnNullWhenPoppingMissingList() {

        RedisStorage storage = new RedisStorage();

        try {

            assertNull(
                    storage.popLeft("missing"));

            assertNull(
                    storage.popRight("missing"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldRejectPopOnString() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.set("name", "durga");

            assertThrows(
                    IllegalStateException.class,
                    () -> storage.popLeft("name"));

            assertThrows(
                    IllegalStateException.class,
                    () -> storage.popRight("name"));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldReturnFullList() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "A");
            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "C");

            assertEquals(
                    List.of("C", "B", "A"),
                    storage.listRange("queue", 0, -1));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldReturnPartialRange() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushLeft("queue", "A");
            storage.pushLeft("queue", "B");
            storage.pushLeft("queue", "C");

            assertEquals(
                    List.of("C", "B"),
                    storage.listRange("queue", 0, 1));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldHandleNegativeIndexes() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.pushRight("queue", "A");
            storage.pushRight("queue", "B");
            storage.pushRight("queue", "C");

            assertEquals(
                    List.of("B", "C"),
                    storage.listRange("queue", -2, -1));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void shouldRejectRangeOnString() {

        RedisStorage storage = new RedisStorage();

        try {

            storage.set("name", "durga");

            assertThrows(
                    IllegalStateException.class,
                    () -> storage.listRange(
                            "name",
                            0,
                            -1));

        } finally {
            storage.shutDown();
        }
    }

    @Test
    void removeExistingSetMember() {
        try {
            RedisStorage store = new RedisStorage();

            store.addToSet("users", "ram");

            assertEquals(1, store.removeFromSet("users", "ram"));
            assertEquals(0, store.removeFromSet("users", "ram"));

            store.shutDown();
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    void getSetMembersReturnsAllMembers() {
        RedisStorage store = new RedisStorage();

        try {
            store.addToSet("users", "ram");
            store.addToSet("users", "shyam");

            Set<String> members = store.getSetMembers("users");

            assertEquals(Set.of("ram", "shyam"), members);

        } finally {
            store.shutDown();
        }
    }

    @Test
    void getSetMembersThrowsWrongTypeForString() {
        RedisStorage store = new RedisStorage();

        try {
            store.set("name", "ram");

            assertThrows(
                    IllegalStateException.class,
                    () -> store.getSetMembers("name"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void getSetMembersReturnsEmptySetForMissingKey() {
        RedisStorage store = new RedisStorage();

        try {
            assertEquals(Set.of(), store.getSetMembers("users"));
        } finally {
            store.shutDown();
        }
    }

    @Test
    void setHashFieldCreatesAndUpdatesField() {
        RedisStorage store = new RedisStorage();

        try {
            assertEquals(1, store.setHashField("user", "name", "ram"));

            assertEquals(0, store.setHashField("user", "name", "shyam"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void setHashFieldThrowsWrongType() {
        RedisStorage store = new RedisStorage();

        try {
            store.set("name", "ram");

            assertThrows(
                    IllegalStateException.class,
                    () -> store.setHashField("name", "age", "20"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void getHashFieldReturnsValue() {
        RedisStorage store = new RedisStorage();

        try {
            store.setHashField("user", "name", "ram");

            assertEquals(
                    "ram",
                    store.getHashField("user", "name"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void getHashFieldReturnsNullWhenMissing() {
        RedisStorage store = new RedisStorage();

        try {
            assertNull(
                    store.getHashField("user", "name"));

            store.setHashField("user", "age", "20");

            assertNull(
                    store.getHashField("user", "name"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void getHashFieldThrowsWrongType() {
        RedisStorage store = new RedisStorage();

        try {
            store.set("name", "ram");

            assertThrows(
                    IllegalStateException.class,
                    () -> store.getHashField("name", "first"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void deleteHashFieldRemovesExistingField() {
        RedisStorage store = new RedisStorage();

        try {
            store.setHashField("user", "name", "ram");

            assertEquals(1, store.deleteHashField("user", "name"));
            assertNull(store.getHashField("user", "name"));

            assertEquals(0, store.deleteHashField("user", "name"));

        } finally {
            store.shutDown();
        }
    }

    @Test
    void deleteHashFieldReturnsZeroForMissingKey() {
        RedisStorage store = new RedisStorage();

        try {
            assertEquals(0, store.deleteHashField("user", "name"));
        } finally {
            store.shutDown();
        }
    }

    @Test
    void deleteHashFieldThrowsWrongType() {
        RedisStorage store = new RedisStorage();

        try {
            store.set("name", "ram");

            assertThrows(
                    IllegalStateException.class,
                    () -> store.deleteHashField("name", "field"));

        } finally {
            store.shutDown();
        }
    }
}