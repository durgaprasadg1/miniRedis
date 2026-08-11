package com.miniredis.command.handler;

import com.miniredis.command.Command;
import com.miniredis.command.CommandHandler;

public class PingHandler implements CommandHandler {

    @Override
    public String execute(Command command) {
        return "PONG";
    }
}
