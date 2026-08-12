package com.miniredis.protocol;

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
}
