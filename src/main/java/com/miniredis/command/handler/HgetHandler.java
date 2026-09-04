package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class HgetHandler implements CommandHandler {

    private final RedisStorage store;

    public HgetHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {
        if (command.getArguments().size() != 2) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);
        String field = command.getArguments().get(1);

        try {
            String value = store.getHashField(key, field);

            return value == null ? "(nil)" : value;

        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}