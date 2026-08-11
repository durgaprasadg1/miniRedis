package com.miniredis.command;

import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public Command parse(String request) {
        String[] parts = request.trim().split("\\s+", 3);

        String name = parts[0].toUpperCase();

        List<String> arguments = Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));

        return new Command(name, arguments);
    }
}
