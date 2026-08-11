package com.miniredis.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandParserTest {

    @Test
    void shouldParsePing() {

        CommandParser parser = new CommandParser();

        Command command = parser.parse("PING");

        assertEquals("PING", command.getName());
        assertTrue(command.getArguments().isEmpty());
    }

    @Test
    void shouldParseGet() {

        CommandParser parser = new CommandParser();

        Command command = parser.parse("GET name");

        assertEquals("GET", command.getName());

        assertEquals(
                1,
                command.getArguments().size());

        assertEquals(
                "name",
                command.getArguments().get(0));
    }

    @Test
    void shouldParseSet() {

        CommandParser parser = new CommandParser();

        Command command = parser.parse("SET name durga");

        assertEquals("SET", command.getName());

        assertEquals(
                2,
                command.getArguments().size());

        assertEquals(
                "name",
                command.getArguments().get(0));

        assertEquals(
                "durga",
                command.getArguments().get(1));
    }

    @Test
    void shouldHandleExtraWhitespace() {

        CommandParser parser = new CommandParser();

        Command command = parser.parse("   SET    name    durga   ");

        assertEquals("SET", command.getName());

        assertEquals("name", command.getArguments().get(0));

        assertEquals("durga", command.getArguments().get(1));
    }
}