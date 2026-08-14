package com.miniredis.protocol;

import java.io.BufferedReader;
import java.io.IOException;

import com.miniredis.command.Command;
import com.miniredis.command.CommandParser;

public class Decoder {
    private final CommandParser parser;

    public Decoder(CommandParser parser) {
        this.parser = parser;
    }

    public Command decode(String request) {
        return parser.parse(request);
    }

    public Command decode(BufferedReader reader) throws IOException {

        String request = reader.readLine();

        if (request == null) {
            return null;
        }

        return decode(request);
    }
}
