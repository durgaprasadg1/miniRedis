package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class RpopHandler implements CommandHandler {

    private final RedisStorage store;

    public RpopHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {

            String value = store.popRight(key);

            if (value == null) {
                return "(nil)";
            }

            return value;

        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}