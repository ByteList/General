package de.gamechest.commands;

import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by ByteList on 25.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class MeCommand extends GCCommand {

    public MeCommand() {
        super("me");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§6You are you and you play at Game-Chest.de!");
    }
}
