package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

public class SizeHandler implements CommandHandler {

    private final RedisStorage store;

    public SizeHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (!command.getArguments().isEmpty()) {
            return "ERR wrong number of arguments";
        }

        return String.valueOf(
                store.size());
    }
}