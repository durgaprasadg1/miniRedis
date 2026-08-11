package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.storage.RedisStorage;
import com.miniredis.command.CommandHandler;

public class SetHandler implements CommandHandler {
    private final RedisStorage store;

    public SetHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 2) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);
        String val = command.getArguments().get(1);

        store.set(key, val);
        return "OK";
    }
}
