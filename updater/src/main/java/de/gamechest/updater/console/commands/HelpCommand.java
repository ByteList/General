package de.gamechest.updater.console.commands;

import de.gamechest.updater.Updater;
import de.gamechest.updater.console.Command;
import de.gamechest.updater.console.CommandHandler;

/**
 * Created by ByteList on 10.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "list all commands");
    }

    private final CommandHandler commandHandler = Updater.getInstance().getCommandHandler();

    @Override
    public void execute(String[] args) {
        System.out.println("All cloud commands: ");
        for(String command : commandHandler.getCommands().keySet()) {
            System.out.println(command+" - "+commandHandler.getCommand(command).getDescription());
        }
    }
}
