package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;
import com.miniredis.storage.RedisStorage;

import java.util.List;

public class LrangeHandler implements CommandHandler {

    private final RedisStorage store;

    public LrangeHandler(RedisStorage store) {
        this.store = store;
    }

    @Override
    public String execute(Command command) {

        if (command.getArguments().size() != 3) {
            return "ERR wrong number of arguments";
        }

        String key = command.getArguments().get(0);

        try {

            int start = Integer.parseInt(
                    command.getArguments().get(1));

            int stop = Integer.parseInt(
                    command.getArguments().get(2));

            List<String> result = store.listRange(
                    key,
                    start,
                    stop);

            if (result.isEmpty()) {
                return "(empty list)";
            }

            return String.join(" ", result);

        } catch (NumberFormatException e) {
            return "ERR value is not an integer";

        } catch (IllegalStateException e) {
            return "WRONGTYPE";
        }
    }
}