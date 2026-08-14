package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.storage.RedisStorage;
import com.miniredis.command.CommandHandler;

public class GetHandler implements CommandHandler {
    private final RedisStorage store;

    public GetHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }
        String key = command.getArguments().get(0);

        String val = store.get(key);

        if (val == null) {
            return "(nil)";
        }
        return val;
    }
}
