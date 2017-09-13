package de.gamechest.updater.console.commands;

import de.gamechest.updater.Updater;
import de.gamechest.updater.console.Command;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class EndCommand extends Command {

    public EndCommand() {
        super("end", "shutdown the updater");
    }

    @Override
    public void execute(String[] args) {
        Updater.getInstance().stop();
    }
}
