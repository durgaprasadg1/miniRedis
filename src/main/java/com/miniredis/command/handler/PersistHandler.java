package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class PersistHandler implements CommandHandler {

    private final RedisStorage store;

    public PersistHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        return String.valueOf(
                store.persist(key));
    }

}