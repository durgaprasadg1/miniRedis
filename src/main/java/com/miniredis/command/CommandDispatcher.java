package com.miniredis.command;

import com.miniredis.storage.RedisStorage;
import com.miniredis.command.handler.SizeHandler;
import com.miniredis.command.handler.DeleteHandler;
import com.miniredis.command.handler.ExistsHandler;
import com.miniredis.command.handler.GetHandler;
import com.miniredis.command.handler.IncrementHandler;
import com.miniredis.command.handler.PingHandler;
import com.miniredis.command.handler.SetHandler;
import com.miniredis.command.handler.DecrementHandler;
import com.miniredis.command.handler.IncrementByHandler;
import com.miniredis.command.handler.DecrementByHandler;
import com.miniredis.command.handler.ExpireHandler;
import com.miniredis.command.handler.TtlHandler;
import com.miniredis.command.handler.PersistHandler;
import com.miniredis.command.handler.LpushHandler;
import com.miniredis.command.handler.RpushHandler;

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
        handlers.put("DECR", new DecrementHandler(store));
        handlers.put("INCRBY", new IncrementByHandler(store));
        handlers.put("DECRBY", new DecrementByHandler(store));
        handlers.put("EXPIRE", new ExpireHandler(store));
        handlers.put("TTL", new TtlHandler(store));
        handlers.put("PERSIST", new PersistHandler(store));
        handlers.put("LPUSH", new LpushHandler(store));
        handlers.put("RPUSH", new RpushHandler(store));
    }

    public String execute(Command command) {

        CommandHandler handler = handlers.get(command.getName());
        if (handler == null) {
            return "ERR unknown command";
        }
        return handler.execute(command);
    }

}
