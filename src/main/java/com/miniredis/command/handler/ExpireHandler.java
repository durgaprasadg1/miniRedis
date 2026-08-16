package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

// import 
public class ExpireHandler implements CommandHandler {

    private final RedisStorage store;

    public ExpireHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {
        if (command.getArguments().size() != 2) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);
        try {
            long seconds = Long.parseLong(command.getArguments().get(1));
            return String.valueOf(store.expire(key, seconds));

        } catch (NumberFormatException e) {
            return "ERR value is not an integer";
        }
    }

}
