package de.gamechest.updater.bootstrap;

import de.gamechest.updater.Updater;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class UpdaterLauncher {

    public static void main(String[] args) throws Exception {
        Updater updater = new Updater();
        updater.getLogger().info("Enabled Updater version " + updater.getVersion() + ".");
        updater.start();
        updater.startStopThread();

        String line;
        while (updater.isRunning && (line = updater.getConsoleReader().readLine(">")) != null) {
            if (!updater.getCommandHandler().dispatchCommand(line)) {
                updater.getLogger().info("** Command not found");
            }
        }
    }
}
