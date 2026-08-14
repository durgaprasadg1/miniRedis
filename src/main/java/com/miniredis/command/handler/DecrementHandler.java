package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class DecrementHandler implements CommandHandler {

    private final RedisStorage store;

    public DecrementHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {
            return String.valueOf(
                    store.change(key, -1));
        } catch (NumberFormatException e) {
            return "ERR value is not an integer";
        }
    }
}