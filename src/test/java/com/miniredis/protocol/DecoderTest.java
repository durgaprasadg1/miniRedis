package com.miniredis.protocol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.miniredis.command.Command;
import com.miniredis.command.CommandParser;

public class DecoderTest {

    @Test
    void shouldDecodeSetCommand() {

        Decoder decoder = new Decoder(new CommandParser());

        Command command = decoder.decode("SET name durga");

        assertEquals("SET", command.getName());

        assertEquals(
                "name",
                command.getArguments().get(0));

        assertEquals(
                "durga",
                command.getArguments().get(1));

    }

    @Test
    void shouldEncodeResponse() {

        Encoder encoder = new Encoder();

        assertEquals(
                "PONG\n",
                encoder.encode("PONG"));
    }
}
