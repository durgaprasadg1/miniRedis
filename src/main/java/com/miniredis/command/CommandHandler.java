package com.miniredis.command;

public interface CommandHandler {

    String execute(Command command);

}