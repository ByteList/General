package de.gamechest.commands;

import de.gamechest.commands.base.GCCommand;
import net.md_5.bungee.api.CommandSender;

/**
 * Created by ByteList on 21.03.2017.
 */
public class VersionCommand extends GCCommand {

    public VersionCommand() {
        super("version", "ver");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String ver = "This server is running ChestSpigot version 1.2.1 (Minecraft version 1.9.x-1.11.x, modified by ByteList)";
        sender.sendMessage(ver);
    }

}
