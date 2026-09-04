package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class HsetHandler implements CommandHandler {

    private final RedisStorage store;

    public HsetHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {
        if (command.getArguments().size() != 3) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);
        String field = command.getArguments().get(1);
        String value = command.getArguments().get(2);

        try {
            return String.valueOf(
                    store.setHashField(key, field, value));
        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}