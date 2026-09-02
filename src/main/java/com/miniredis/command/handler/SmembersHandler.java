package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

import java.util.Set;

public class SmembersHandler implements CommandHandler {

    private final RedisStorage store;

    public SmembersHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {
            Set<String> members = store.getSetMembers(key);

            if (members.isEmpty()) {
                return "(empty set)";
            }

            return String.join(" ", members);

        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}