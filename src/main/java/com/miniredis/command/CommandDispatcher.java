package com.miniredis.command;

import com.miniredis.storage.RedisStorage;
import com.miniredis.command.handler.SizeHandler;
import com.miniredis.command.handler.DeleteHandler;
import com.miniredis.command.handler.ExistsHandler;
import com.miniredis.command.handler.GetHandler;
import com.miniredis.command.handler.IncrementHandler;
import com.miniredis.command.handler.PingHandler;
import com.miniredis.command.handler.SetHandler;

import java.util.HashMap;
import java.util.Map;

public class CommandDispatcher {
    private final Map<String, CommandHandler> handlers;

    public CommandDispatcher(RedisStorage store) {
        handlers = new HashMap<>();

        handlers.put("PING", new PingHandler());
        handlers.put("SET", new SetHandler(store));
        handlers.put("GET", new GetHandler(store));
        handlers.put("DEL", new DeleteHandler(store));
        handlers.put("EXISTS", new ExistsHandler(store));
        handlers.put("SIZE", new SizeHandler(store));
        handlers.put("INCR", new IncrementHandler(store));
    }

    public String execute(Command command) {

        CommandHandler handler = handlers.get(command.getName());
        if (handler == null) {
            return "ERR unknown command";
        }
        return handler.execute(command);
    }

}
