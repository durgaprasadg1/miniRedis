package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class IncrementByHandler implements CommandHandler {

    private final RedisStorage store;

    public IncrementByHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 2) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {

            int amount = Integer.parseInt(
                    command.getArguments().get(1));

            return String.valueOf(
                    store.change(key, amount));

        } catch (NumberFormatException e) {
            return "ERR value is not an integer";
        }
    }
}