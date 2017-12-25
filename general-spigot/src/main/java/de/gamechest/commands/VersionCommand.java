package de.gamechest.commands;

import de.gamechest.GameChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by ByteList on 25.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class VersionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String[] version = GameChest.getInstance().getVersion().split(":");
        sender.sendMessage("This server is running GameChest version "+version[0]+" (Git: "+version[1]+", by ByteList)");
        return true;
    }
}
