package com.miniredis.command;

import com.miniredis.storage.RedisStorage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandDispatcherTest {

    @Test
    void shouldExecutePing() {

        RedisStorage storage = new RedisStorage();

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command("PING", List.of());

        String response = dispatcher.execute(command);

        assertEquals("PONG", response);
    }

    @Test
    void shouldExecuteSet() {

        RedisStorage storage = new RedisStorage();

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "SET",
                List.of("name", "durga"));

        String response = dispatcher.execute(command);

        assertEquals("OK", response);

        assertEquals(
                "durga",
                storage.get("name"));
    }

    @Test
    void shouldExecuteGet() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "GET",
                List.of("name"));

        String response = dispatcher.execute(command);

        assertEquals("durga", response);
    }

    @Test
    void shouldRejectUnknownCommand() {

        RedisStorage storage = new RedisStorage();

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "HELLO",
                List.of());

        String response = dispatcher.execute(command);

        assertEquals(
                "ERR unknown command",
                response);
    }

    @Test
    void shouldExecuteDelete() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "DEL",
                List.of("name"));

        String response = dispatcher.execute(command);

        assertEquals("1", response);
        assertNull(storage.get("name"));
    }

    @Test
    void shouldReturnZeroWhenDeletingMissingKey() {

        RedisStorage storage = new RedisStorage();

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "DEL",
                List.of("missing"));

        String response = dispatcher.execute(command);

        assertEquals("0", response);
    }

    @Test
    void shouldExecuteExists() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "EXISTS",
                List.of("name"));

        String response = dispatcher.execute(command);

        assertEquals("1", response);
    }

    @Test
    void shouldReturnZeroForMissingKey() {

        RedisStorage storage = new RedisStorage();

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "EXISTS",
                List.of("missing"));

        String response = dispatcher.execute(command);

        assertEquals("0", response);
    }

    @Test
    void shouldExecuteDbSize() {

        RedisStorage storage = new RedisStorage();

        storage.set("a", "10");
        storage.set("b", "20");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command("SIZE", List.of());

        String response = dispatcher.execute(command);

        assertEquals("2", response);
    }

    @Test
    void shouldExecuteIncrement() {

        RedisStorage storage = new RedisStorage();

        storage.set("counter", "10");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "INCR",
                List.of("counter"));

        String response = dispatcher.execute(command);

        assertEquals("11", response);
    }

    @Test
    void shouldRejectNonIntegerValue() {

        RedisStorage storage = new RedisStorage();

        storage.set("name", "durga");

        CommandDispatcher dispatcher = new CommandDispatcher(storage);

        Command command = new Command(
                "INCR",
                List.of("name"));

        String response = dispatcher.execute(command);

        assertEquals(
                "ERR value is not an integer",
                response);
    }
}