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

        @Test
        void shouldExecuteDecr() {

                RedisStorage storage = new RedisStorage();

                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("counter", "10")));

                String response = dispatcher.execute(
                                new Command(
                                                "DECR",
                                                List.of("counter")));

                assertEquals("9", response);
        }

        @Test
        void shouldExecuteIncrBy() {

                RedisStorage storage = new RedisStorage();

                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("counter", "10")));

                String response = dispatcher.execute(
                                new Command(
                                                "INCRBY",
                                                List.of("counter", "5")));

                assertEquals("15", response);
        }

        @Test
        void shouldExecuteDecrBy() {

                RedisStorage storage = new RedisStorage();

                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("counter", "10")));

                String response = dispatcher.execute(
                                new Command(
                                                "DECRBY",
                                                List.of("counter", "4")));

                assertEquals("6", response);
        }

        @Test
        void shouldExecuteExpire() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                String response = dispatcher.execute(
                                new Command(
                                                "EXPIRE",
                                                List.of("name", "10")));

                assertEquals("1", response);
        }

        @Test
        void shouldExecuteTtl() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                dispatcher.execute(
                                new Command(
                                                "EXPIRE",
                                                List.of("name", "10")));

                String response = dispatcher.execute(
                                new Command(
                                                "TTL",
                                                List.of("name")));

                assertTrue(
                                Integer.parseInt(response) > 0);
        }

        @Test
        void shouldExecutePersist() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                dispatcher.execute(
                                new Command(
                                                "EXPIRE",
                                                List.of("name", "10")));

                String response = dispatcher.execute(
                                new Command(
                                                "PERSIST",
                                                List.of("name")));

                assertEquals("1", response);

                assertEquals(
                                "-1",
                                dispatcher.execute(
                                                new Command(
                                                                "TTL",
                                                                List.of("name"))));
        }

        @Test
        void shouldExecuteLpush() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                String response = dispatcher.execute(
                                new Command(
                                                "LPUSH",
                                                List.of("queue", "A")));

                assertEquals("1", response);
        }

        @Test
        void shouldExecuteRpush() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                String response = dispatcher.execute(
                                new Command(
                                                "RPUSH",
                                                List.of("queue", "A")));

                assertEquals("1", response);
        }

        @Test
        void shouldRejectLpushOnString() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                String response = dispatcher.execute(
                                new Command(
                                                "LPUSH",
                                                List.of("name", "hello")));

                assertEquals("WRONGTYPE", response);
        }

        @Test
        void shouldExecuteLlen() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "LPUSH",
                                                List.of("queue", "A")));

                dispatcher.execute(
                                new Command(
                                                "RPUSH",
                                                List.of("queue", "B")));

                String response = dispatcher.execute(
                                new Command(
                                                "LLEN",
                                                List.of("queue")));

                assertEquals("2", response);
        }

        @Test
        void shouldReturnZeroForMissingList() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                String response = dispatcher.execute(
                                new Command(
                                                "LLEN",
                                                List.of("missing")));

                assertEquals("0", response);
        }

        @Test
        void shouldRejectLlenOnString() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                String response = dispatcher.execute(
                                new Command(
                                                "LLEN",
                                                List.of("name")));

                assertEquals("WRONGTYPE", response);
        }

        @Test
        void shouldExecuteLpop() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "LPUSH",
                                                List.of("queue", "A")));

                dispatcher.execute(
                                new Command(
                                                "LPUSH",
                                                List.of("queue", "B")));

                String response = dispatcher.execute(
                                new Command(
                                                "LPOP",
                                                List.of("queue")));

                assertEquals("B", response);
        }

        @Test
        void shouldExecuteRpop() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "RPUSH",
                                                List.of("queue", "A")));

                dispatcher.execute(
                                new Command(
                                                "RPUSH",
                                                List.of("queue", "B")));

                String response = dispatcher.execute(
                                new Command(
                                                "RPOP",
                                                List.of("queue")));

                assertEquals("B", response);
        }

        @Test
        void shouldReturnNilForMissingList() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                assertEquals(
                                "(nil)",
                                dispatcher.execute(
                                                new Command(
                                                                "LPOP",
                                                                List.of("missing"))));

                assertEquals(
                                "(nil)",
                                dispatcher.execute(
                                                new Command(
                                                                "RPOP",
                                                                List.of("missing"))));
        }

        @Test
        void shouldRejectPopOnString() {

                RedisStorage storage = new RedisStorage();
                CommandDispatcher dispatcher = new CommandDispatcher(storage);

                dispatcher.execute(
                                new Command(
                                                "SET",
                                                List.of("name", "durga")));

                assertEquals(
                                "WRONGTYPE",
                                dispatcher.execute(
                                                new Command(
                                                                "LPOP",
                                                                List.of("name"))));
        }
}