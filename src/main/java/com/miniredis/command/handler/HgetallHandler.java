package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

import java.util.Map;

public class HgetallHandler implements CommandHandler {

    private final RedisStorage store;

    public HgetallHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {
        if (command.getArguments().size() != 1) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {
            Map<String, String> hash = store.getAllHashFields(key);

            if (hash.isEmpty()) {
                return "(empty hash)";
            }

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : hash.entrySet()) {
                if (result.length() > 0) {
                    result.append(" ");
                }

                result.append(entry.getKey())
                        .append(" ")
                        .append(entry.getValue());
            }

            return result.toString();

        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}