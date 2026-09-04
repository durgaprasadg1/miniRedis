package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class SetHandler implements CommandHandler {

    private final RedisStorage store;

    public SetHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 2 &&
                command.getArguments().size() != 3) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);
        String value = command.getArguments().get(1);

        if (command.getArguments().size() == 2) {
            store.set(key, value);
            return "OK";
        }

        try {
            long seconds = Long.parseLong(
                    command.getArguments().get(2));

            if (seconds < 0) {
                return "ERR invalid expire time";
            }

            store.set(key, value);
            store.expire(key, seconds);

            return "OK";

        } catch (NumberFormatException e) {
            return "ERR invalid expire time";
        }
    }
}