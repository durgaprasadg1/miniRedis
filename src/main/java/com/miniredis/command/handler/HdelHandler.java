package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class HdelHandler implements CommandHandler {

    private final RedisStorage store;

    public HdelHandler(RedisStorage store) {
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
            return String.valueOf(
                    store.deleteHashField(key, field));
        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}